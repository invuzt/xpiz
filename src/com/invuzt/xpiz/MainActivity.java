package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Log;
import android.util.Size;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("hello");
    }

    private native String analyzeFrame(byte[] data, int width, int height);
    private CameraDevice cameraDevice;
    private TextureView textureView;
    private ImageView lastCapturePreview;
    private Size previewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        textureView = new TextureView(this);
        // Penting: Pastikan Opaque agar tidak tembus pandang (Putih/Hitam)
        textureView.setOpaque(true); 
        root.addView(textureView);

        lastCapturePreview = new ImageView(this);
        int pSize = 300;
        FrameLayout.LayoutParams galleryParams = new FrameLayout.LayoutParams(pSize, pSize);
        galleryParams.gravity = Gravity.BOTTOM | Gravity.START;
        galleryParams.setMargins(50, 0, 0, 100);
        lastCapturePreview.setLayoutParams(galleryParams);
        // Kasih background gelap agar kalau foto gagal, kelihatan bedanya
        lastCapturePreview.setBackgroundColor(Color.BLACK); 
        lastCapturePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        root.addView(lastCapturePreview);

        Button shutter = new Button(this);
        int sSize = 200;
        FrameLayout.LayoutParams shutterParams = new FrameLayout.LayoutParams(sSize, sSize);
        shutterParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        shutterParams.bottomMargin = 100;
        shutter.setLayoutParams(shutterParams);
        shutter.setBackgroundColor(Color.WHITE);
        shutter.setAlpha(0.7f);
        
        shutter.setOnClickListener(v -> takePicture());
        root.addView(shutter);

        setContentView(root);

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                }
            }
            @Override public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) { configureTransform(w, h); }
            @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) { return true; }
            @Override public void onSurfaceTextureUpdated(SurfaceTexture st) {}
        });
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cid = manager.getCameraIdList()[0];
            CameraCharacteristics chars = manager.getCameraCharacteristics(cid);
            StreamConfigurationMap map = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            
            previewSize = Collections.max(Arrays.asList(map.getOutputSizes(SurfaceTexture.class)), new Comparator<Size>() {
                @Override public int compare(Size a, Size b) { 
                    return Long.signum((long)a.getWidth()*a.getHeight() - (long)b.getWidth()*b.getHeight()); 
                }
            });

            manager.openCamera(cid, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    startPreview();
                }
                @Override public void onDisconnected(CameraDevice c) { c.close(); }
                @Override public void onError(CameraDevice c, int e) { c.close(); }
            }, null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startPreview() {
        if (cameraDevice == null || !textureView.isAvailable()) return;
        SurfaceTexture st = textureView.getSurfaceTexture();
        st.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(st);
        
        try {
            final CaptureRequest.Builder br = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            br.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try { session.setRepeatingRequest(br.build(), null, null); } catch (Exception e) {}
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) {}
            }, null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void takePicture() {
        if (!textureView.isAvailable()) return;
        
        // Ambil Bitmap dengan ukuran yang sama dengan Preview agar tidak pecah/putih
        Bitmap capture = textureView.getBitmap(previewSize.getWidth(), previewSize.getHeight());
        
        if (capture != null) {
            // Tampilkan ke ImageView (Preview Pojok)
            runOnUiThread(() -> lastCapturePreview.setImageBitmap(capture));
            
            // Simpan ke File
            try {
                File photoFile = new File(getExternalFilesDir(null), "xpiz_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream fos = new FileOutputStream(photoFile);
                capture.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                
                // Sapa Rust
                analyzeFrame(new byte[1], 1, 1);
                Toast.makeText(this, "Tersimpan di: " + photoFile.getName(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("xpiz", "Gagal simpan: " + e.getMessage());
            }
        } else {
            Toast.makeText(this, "Gagal mengambil frame!", Toast.LENGTH_SHORT).show();
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        if (textureView == null || previewSize == null) return;
        Matrix matrix = new Matrix();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) viewHeight / previewSize.getHeight(), (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
    }
}
