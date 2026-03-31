package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.util.Log;
import android.util.Size;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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

        // 1. Container Utama (FrameLayout agar bisa tumpuk)
        FrameLayout root = new FrameLayout(this);
        
        // 2. Layar Kamera (TEXTUREVIEW GANTI SURFACEVIEW)
        textureView = new TextureView(this);
        root.addView(textureView);

        // 3. Galeri Kecil (Untuk Preview Foto yang baru diambil)
        lastCapturePreview = new ImageView(this);
        int pSize = 300;
        FrameLayout.LayoutParams galleryParams = new FrameLayout.LayoutParams(pSize, pSize);
        galleryParams.gravity = Gravity.BOTTOM | Gravity.START;
        galleryParams.setMargins(50, 0, 0, 100);
        lastCapturePreview.setLayoutParams(galleryParams);
        lastCapturePreview.setBackgroundColor(Color.LTGRAY); // Border sementara
        lastCapturePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        root.addView(lastCapturePreview);

        // 4. Tombol Shutter (Bulat Putih)
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
                    openCamera(w, h);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                }
            }
            @Override public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) { configureTransform(w, h); }
            @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) { return true; }
            @Override public void onSurfaceTextureUpdated(SurfaceTexture st) {}
        });
    }

    private void openCamera(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cid = manager.getCameraIdList()[0];
            CameraCharacteristics chars = manager.getCameraCharacteristics(cid);
            StreamConfigurationMap map = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            
            // Pilih ukuran preview terbesar tapi tetap proporsional
            previewSize = Collections.max(Arrays.asList(map.getOutputSizes(SurfaceTexture.class)), new Comparator<Size>() {
                @Override public int compare(Size a, Size b) { return Long.signum((long)a.getWidth()*a.getHeight() - (long)b.getWidth()*b.getHeight()); }
            });

            // Set aspek rasio TextureView agar cocok dengan kamera
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                // Di Portrait, kita balik ukurannya agar rasio sensor dan layar pas
                textureView.setLayoutParams(new FrameLayout.LayoutParams(previewSize.getHeight(), previewSize.getWidth(), Gravity.CENTER));
            } else {
                 textureView.setLayoutParams(new FrameLayout.LayoutParams(previewSize.getWidth(), previewSize.getHeight(), Gravity.CENTER));
            }

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

    // FUNGSI SUPER SAKTI: Mengambil Gambar & Menyimpan
    private void takePicture() {
        if (cameraDevice == null || lastCapturePreview == null) return;
        
        // Ambil frame terakhir dari TextureView
        Bitmap capture = textureView.getBitmap();
        if (capture == null) return;
        
        // Tampilkan langsung di Galeri Kecil (Preview)
        lastCapturePreview.setImageBitmap(capture);
        
        // Logika Simpan (Logika "Ingatan")
        try {
            File photoFile = new File(getExternalFilesDir(null), "xpiz_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(photoFile);
            capture.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            
            // Tes Kirim Data Mentah ke Rust (Simulasi frame)
            analyzeFrame(new byte[100], 10, 10);
            
            Toast.makeText(this, "Foto Disimpan: " + photoFile.getName(), Toast.LENGTH_SHORT).show();
            
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal Menyimpan!", Toast.LENGTH_SHORT).show();
        }
    }

    // FUNGSI ROTASI & TRANSFORM (Untuk TextureView agar tidak penyet)
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
