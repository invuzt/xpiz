package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.util.Log;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.net.Uri;
import java.io.OutputStream;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String analyzeFrame(String path, int w, int h);

    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraSession;
    private TextureView textureView;
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
            @Override public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) {}
            @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) { return true; }
            @Override public void onSurfaceTextureUpdated(SurfaceTexture st) {}
        });
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cid = manager.getCameraIdList()[0];
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) return;
            manager.openCamera(cid, new CameraDevice.StateCallback() {
                @Override public void onOpened(CameraDevice c) { cameraDevice = c; startPreview(); }
                @Override public void onDisconnected(CameraDevice c) { c.close(); }
                @Override public void onError(CameraDevice c, int e) { c.close(); }
            }, backgroundHandler);
        } catch (Exception e) { Log.e("xpiz", "Gagal buka kamera"); }
    }

    private void startPreview() {
        SurfaceTexture st = textureView.getSurfaceTexture();
        Surface surface = new Surface(st);
        try {
            final CaptureRequest.Builder br = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            br.addTarget(surface);
            cameraDevice.createCaptureSession(java.util.Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override public void onConfigured(CameraCaptureSession s) {
                    cameraSession = s;
                    try { s.setRepeatingRequest(br.build(), null, backgroundHandler); } catch (Exception e) {}
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) {}
            }, backgroundHandler);
        } catch (Exception e) {}
    }

    private void takePicture() {
        // Ambil bitmap sekarang juga di UI Thread biar gak Putih layarnya
        Bitmap bmp = textureView.getBitmap();
        if (bmp == null) return;

        backgroundHandler.post(() -> {
            try {
                String fileName = "xpiz_" + System.currentTimeMillis() + ".jpg";
                ContentValues v = new ContentValues();
                v.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                v.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                v.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/xpiz");
                
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
                if (uri != null) {
                    try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        
                        // Kabari Rust untuk editor nanti
                        String report = analyzeFrame(fileName, bmp.getWidth(), bmp.getHeight());
                        runOnUiThread(() -> Toast.makeText(this, "Berhasil! " + report, Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (Exception e) {
                Log.e("xpiz", "Gagal simpan: " + e.getMessage());
            }
        });
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CamBack");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
}
