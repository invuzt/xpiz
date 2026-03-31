package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Log;
import android.util.Size;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.net.Uri;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }

    private native String analyzeFrame(byte[] data, int width, int height);
    private CameraDevice cameraDevice;
    private TextureView textureView;
    private ImageView lastCapturePreview;
    private Size previewSize;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBackgroundThread();

        FrameLayout root = new FrameLayout(this);
        textureView = new TextureView(this);
        textureView.setOpaque(true);
        root.addView(textureView);

        lastCapturePreview = new ImageView(this);
        FrameLayout.LayoutParams galleryParams = new FrameLayout.LayoutParams(300, 300);
        galleryParams.gravity = Gravity.BOTTOM | Gravity.START;
        galleryParams.setMargins(50, 0, 0, 100);
        lastCapturePreview.setLayoutParams(galleryParams);
        lastCapturePreview.setBackgroundColor(Color.BLACK);
        lastCapturePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        root.addView(lastCapturePreview);

        Button shutter = new Button(this);
        FrameLayout.LayoutParams shutterParams = new FrameLayout.LayoutParams(200, 200);
        shutterParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        shutterParams.bottomMargin = 100;
        shutter.setLayoutParams(shutterParams);
        shutter.setBackgroundColor(Color.WHITE);
        shutter.setAlpha(0.6f);
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
            previewSize = Collections.max(Arrays.asList(map.getOutputSizes(SurfaceTexture.class)), (a, b) -> 
                Long.signum((long)a.getWidth()*a.getHeight() - (long)b.getWidth()*b.getHeight()));

            manager.openCamera(cid, new CameraDevice.StateCallback() {
                @Override public void onOpened(CameraDevice camera) { cameraDevice = camera; startPreview(); }
                @Override public void onDisconnected(CameraDevice c) { c.close(); }
                @Override public void onError(CameraDevice c, int e) { c.close(); }
            }, backgroundHandler);
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
                @Override public void onConfigured(CameraCaptureSession session) {
                    try { session.setRepeatingRequest(br.build(), null, backgroundHandler); } catch (Exception e) {}
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) {}
            }, backgroundHandler);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void takePicture() {
        // Ambil bitmap di UI thread sebentar saja
        Bitmap bitmap = textureView.getBitmap();
        if (bitmap == null) return;
        lastCapturePreview.setImageBitmap(bitmap);

        // Oper sisa kerja berat (simpan file) ke background biar gak lag!
        backgroundHandler.post(() -> {
            saveImageToGallery(bitmap);
            analyzeFrame(new byte[1], 1, 1);
        });
    }

    private void saveImageToGallery(Bitmap bitmap) {
        String name = "xpiz_" + System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/xpiz");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
            runOnUiThread(() -> Toast.makeText(this, "Tersimpan di Galeri!", Toast.LENGTH_SHORT).show());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        if (textureView == null || previewSize == null) return;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
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
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    @Override protected void onPause() {
        backgroundThread.quitSafely();
        super.onPause();
    }
}
