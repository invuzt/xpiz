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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SurfaceView sv = new SurfaceView(this);
        sv.getHolder().setKeepScreenOn(true);
        setContentView(sv);

        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                checkPermissionAndOpen(holder);
            }
            @Override public void surfaceChanged(SurfaceHolder h, int f, int w, int h1) {}
            @Override public void surfaceDestroyed(SurfaceHolder h) { closeCamera(); }
        });
    }

    private void checkPermissionAndOpen(SurfaceHolder holder) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            openBestCamera(holder);
        }
    }

    private void openBestCamera(SurfaceHolder holder) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            // Cari Kamera Belakang (Lens Facing Back)
            String targetId = null;
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics chars = manager.getCameraCharacteristics(id);
                Integer facing = chars.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    targetId = id;
                    break;
                }
            }
            if (targetId == null) targetId = manager.getCameraIdList()[0];

            manager.openCamera(targetId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    startStreaming(holder.getSurface());
                }
                @Override public void onDisconnected(CameraDevice c) { closeCamera(); }
                @Override public void onError(CameraDevice c, int e) { closeCamera(); }
            }, null);
        } catch (Exception e) { 
            Log.e("xpiz", "Gagal buka kamera: " + e.getMessage());
        }
    }

    private void startStreaming(Surface surface) {
        try {
            final CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);
            // Paksa Mode Auto agar sensor 'bangun'
            builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(builder.build(), null, null);
                        Log.d("xpiz", "Streaming Aktif!");
                    } catch (Exception e) { e.printStackTrace(); }
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) {}
            }, null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int rc, String[] p, int[] gr) {
        if (rc == 101 && gr.length > 0 && gr[0] == PackageManager.PERMISSION_GRANTED) {
            recreate();
        }
    }
}
