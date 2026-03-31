package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.view.Surface;
import android.util.Log;
import java.util.Arrays;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("hello");
    }

    private native String analyzeFrame(byte[] data, int width, int height);
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SurfaceView sv = new SurfaceView(this);
        // Paksa SurfaceView agar tetap aktif
        sv.getHolder().setKeepScreenOn(true);
        setContentView(sv);

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initCamera(sv.getHolder());
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    private void initCamera(SurfaceHolder holder) {
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder h) {
                openCamera(h);
            }
            @Override public void surfaceChanged(SurfaceHolder h, int f, int w, int h1) {}
            @Override public void surfaceDestroyed(SurfaceHolder h) {
                if (cameraDevice != null) cameraDevice.close();
            }
        });
    }

    private void openCamera(SurfaceHolder holder) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[0]; // Kamera belakang
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    startPreview(holder.getSurface());
                }
                @Override public void onDisconnected(CameraDevice camera) { camera.close(); }
                @Override public void onError(CameraDevice camera, int error) { camera.close(); }
            }, null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startPreview(Surface surface) {
        try {
            // 1. Buat Request untuk PREVIEW
            final CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);

            // 2. Buat Session
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    cameraSession = session;
                    try {
                        // 3. SET REPEATING REQUEST (Kunci agar layar tidak hitam)
                        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        cameraSession.setRepeatingRequest(builder.build(), null, null);
                        
                        // Tes Rust Analysis
                        analyzeFrame(new byte[10], 1, 1);
                    } catch (CameraAccessException e) { e.printStackTrace(); }
                }
                @Override public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e("xpiz", "Konfigurasi Kamera Gagal!");
                }
            }, null);
        } catch (CameraAccessException e) { e.printStackTrace(); }
    }
}
