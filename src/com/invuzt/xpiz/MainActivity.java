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
import android.hardware.camera2.params.MeteringRectangle;
import android.util.Size;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.net.Uri;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String analyzeFrame(byte[] data, int width, int height);

    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraSession;
    private CaptureRequest.Builder previewBuilder;
    private TextureView textureView;
    private Size previewSize;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBackgroundThread();

        FrameLayout root = new FrameLayout(this);
        textureView = new TextureView(this);
        
        // Manual Focus saat layar disentuh
        textureView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                triggerFocus(event.getX(), event.getY());
            }
            return true;
        });

        root.addView(textureView);

        // Tombol Shutter
        Button shutter = new Button(this);
        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(220, 220);
        btnParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        btnParams.bottomMargin = 80;
        shutter.setLayoutParams(btnParams);
        shutter.setBackgroundColor(Color.parseColor("#CCFFFFFF"));
        shutter.setOnClickListener(v -> takePicture());
        root.addView(shutter);

        setContentView(root);

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) { openCamera(w, h); }
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
            
            // Cari resolusi yang paling mendekati rasio layar agar tidak penyet
            previewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                return;
            }

            manager.openCamera(cid, new CameraDevice.StateCallback() {
                @Override public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    startPreview();
                }
                @Override public void onDisconnected(CameraDevice c) { c.close(); }
                @Override public void onError(CameraDevice c, int e) { c.close(); }
            }, backgroundHandler);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startPreview() {
        SurfaceTexture st = textureView.getSurfaceTexture();
        st.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(st);
        try {
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(surface);
            
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override public void onConfigured(CameraCaptureSession session) {
                    cameraSession = session;
                    try {
                        previewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        cameraSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
                    } catch (Exception e) {}
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) {}
            }, backgroundHandler);
        } catch (Exception e) {}
    }

    private void triggerFocus(float x, float y) {
        if (cameraSession == null) return;
        try {
            // Logika Manual Focus Sederhana
            cameraSession.stopRepeating();
            previewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            cameraSession.capture(previewBuilder.build(), null, backgroundHandler);
            
            // Kembalikan ke mode normal
            previewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
            cameraSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
            Toast.makeText(this, "Fokus dikunci!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void takePicture() {
        backgroundHandler.post(() -> {
            Bitmap bitmap = textureView.getBitmap();
            if (bitmap == null) return;

            // Simpan ke Galeri
            ContentValues v = new ContentValues();
            v.put(MediaStore.Images.Media.DISPLAY_NAME, "xpiz_" + System.currentTimeMillis() + ".jpg");
            v.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            v.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/xpiz");
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
            
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
                // Kabari Rust kalau ada foto baru
                analyzeFrame(new byte[1], 1, 1);
                runOnUiThread(() -> Toast.makeText(this, "Cekrek! Cek Galeri", Toast.LENGTH_SHORT).show());
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void configureTransform(int w, int h) {
        if (previewSize == null) return;
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, w, h);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        bufferRect.offset(viewRect.centerX() - bufferRect.centerX(), viewRect.centerY() - bufferRect.centerY());
        matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
        float scale = Math.max((float) h / previewSize.getHeight(), (float) w / previewSize.getWidth());
        matrix.postScale(scale, scale, viewRect.centerX(), viewRect.centerY());
        textureView.setTransform(matrix);
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBack");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    @Override protected void onPause() {
        if (backgroundThread != null) backgroundThread.quitSafely();
        super.onPause();
    }
}
