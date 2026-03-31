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
import android.util.Size;
import android.media.ImageReader;
import android.media.Image;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.net.Uri;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String analyzeFrame(String path, int w, int h);

    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraSession;
    private TextureView textureView;
    private ImageReader imageReader;
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
        shutter.setBackgroundColor(Color.parseColor("#80FFFFFF")); // Transparan dikit
        shutter.setOnClickListener(v -> takePicture());
        root.addView(shutter);
        
        setContentView(root);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) { openCamera(w, h); }
            @Override public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) {}
            @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) { return true; }
            @Override public void onSurfaceTextureUpdated(SurfaceTexture st) {}
        });
    }

    private void openCamera(int w, int h) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cid = manager.getCameraIdList()[0];
            // Setup ImageReader untuk ambil foto resolusi tinggi di background
            imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(reader -> {
                saveImage(reader.acquireLatestImage());
            }, backgroundHandler);

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
        Surface previewSurface = new Surface(st);
        Surface readerSurface = imageReader.getSurface();
        try {
            final CaptureRequest.Builder br = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            br.addTarget(previewSurface);
            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, readerSurface), new CameraCaptureSession.StateCallback() {
                @Override public void onConfigured(CameraCaptureSession s) {
                    cameraSession = s;
                    try { s.setRepeatingRequest(br.build(), null, backgroundHandler); } catch (Exception e) {}
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) {}
            }, backgroundHandler);
        } catch (Exception e) {}
    }

    private void takePicture() {
        if (cameraDevice == null || cameraSession == null) return;
        try {
            // Ambil satu frame JPEG dari ImageReader
            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            cameraSession.capture(captureBuilder.build(), null, backgroundHandler);
            Toast.makeText(this, "Cekrek!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {}
    }

    private void saveImage(Image img) {
        ByteBuffer buffer = img.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        img.close();

        String fileName = "xpiz_" + System.currentTimeMillis() + ".jpg";
        ContentValues v = new ContentValues();
        v.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        v.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        v.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/xpiz");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);

        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            out.write(bytes);
            // Panggil RUST EDITOR setelah file tersimpan
            String report = analyzeFrame(fileName, 0, 0);
            runOnUiThread(() -> Toast.makeText(this, report, Toast.LENGTH_LONG).show());
        } catch (Exception e) { Log.e("xpiz", "Gagal: " + e.getMessage()); }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CamBack");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
}
