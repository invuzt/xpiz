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
import android.util.Size;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.net.Uri;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
        root.addView(textureView);
        
        Button shutter = new Button(this);
        FrameLayout.LayoutParams btn = new FrameLayout.LayoutParams(220, 220);
        btn.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        btn.bottomMargin = 80;
        shutter.setLayoutParams(btn);
        shutter.setBackgroundColor(Color.WHITE);
        shutter.setOnClickListener(v -> takePicture());
        root.addView(shutter);
        
        setContentView(root);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) { openCamera(); }
            @Override public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) { configureTransform(w, h); }
            @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) { return true; }
            @Override public void onSurfaceTextureUpdated(SurfaceTexture st) {}
        });
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cid = manager.getCameraIdList()[0];
            StreamConfigurationMap map = manager.getCameraCharacteristics(cid).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) return;
            manager.openCamera(cid, new CameraDevice.StateCallback() {
                @Override public void onOpened(CameraDevice c) { cameraDevice = c; startPreview(); }
                @Override public void onDisconnected(CameraDevice c) { c.close(); }
                @Override public void onError(CameraDevice c, int e) { c.close(); }
            }, backgroundHandler);
        } catch (Exception e) {}
    }

    private void startPreview() {
        SurfaceTexture st = textureView.getSurfaceTexture();
        st.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(st);
        try {
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override public void onConfigured(CameraCaptureSession s) {
                    cameraSession = s;
                    try { s.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler); } catch (Exception e) {}
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) {}
            }, backgroundHandler);
        } catch (Exception e) {}
    }

    private void takePicture() {
        // 1. Ambil Bitmap dari TextureView (Bisa jadi format HARDWARE)
        Bitmap rawBmp = textureView.getBitmap();
        if (rawBmp == null) return;

        // 2. PAKSA KONVERSI KE SOFTWARE MEMORY (ARGB_8888)
        // Ini kunci agar Rust & Galeri tidak dapet data hitam/kosong
        Bitmap softwareBmp = rawBmp.copy(Bitmap.Config.ARGB_8888, false);
        rawBmp.recycle(); // Buang yang hardware

        backgroundHandler.post(() -> {
            try {
                // 3. Kirim data asli ke Rust
                ByteBuffer buffer = ByteBuffer.allocate(softwareBmp.getByteCount());
                softwareBmp.copyPixelsToBuffer(buffer);
                String report = analyzeFrame(buffer.array(), softwareBmp.getWidth(), softwareBmp.getHeight());
                
                runOnUiThread(() -> Toast.makeText(this, report, Toast.LENGTH_SHORT).show());
                
                // 4. Simpan ke Galeri
                saveToGallery(softwareBmp);
            } finally {
                // softwareBmp.recycle(); // Opsional, biarkan GC yang urus jika sering crash
            }
        });
    }

    private void saveToGallery(Bitmap bmp) {
        ContentValues v = new ContentValues();
        v.put(MediaStore.Images.Media.DISPLAY_NAME, "xpiz_" + System.currentTimeMillis() + ".jpg");
        v.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        v.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/xpiz");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, out);
        } catch (Exception e) {
            Log.e("xpiz", "Gagal simpan galeri: " + e.getMessage());
        }
    }

    private void configureTransform(int w, int h) {
        if (previewSize == null) return;
        Matrix m = new Matrix();
        RectF vRect = new RectF(0, 0, w, h);
        RectF bRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        bRect.offset(vRect.centerX() - bRect.centerX(), vRect.centerY() - bRect.centerY());
        m.setRectToRect(vRect, bRect, Matrix.ScaleToFit.FILL);
        float s = Math.max((float) h / previewSize.getHeight(), (float) w / previewSize.getWidth());
        m.postScale(s, s, vRect.centerX(), vRect.centerY());
        textureView.setTransform(m);
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CamBack");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
}
