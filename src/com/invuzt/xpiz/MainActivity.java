package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
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

        // 1. Container Utama
        FrameLayout root = new FrameLayout(this);
        
        // 2. Layar Kamera
        SurfaceView sv = new SurfaceView(this);
        sv.getHolder().setKeepScreenOn(true);
        root.addView(sv);

        // 3. Tombol Shutter (Bulat Putih)
        Button shutter = new Button(this);
        int size = 200;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = 100;
        shutter.setLayoutParams(params);
        
        // Styling Tombol biar mirip Shutter
        shutter.setBackgroundColor(Color.WHITE);
        shutter.setAlpha(0.7f);
        
        // Efek Klik
        shutter.setOnClickListener(v -> {
            // Panggil Rust saat tombol diklik!
            String hasil = analyzeFrame(new byte[1024], 32, 32);
            Toast.makeText(this, "Jepret! " + hasil, Toast.LENGTH_SHORT).show();
            
            // Efek visual tombol ditekan
            v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
            });
        });

        root.addView(shutter);
        setContentView(root);

        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera(holder.getSurface());
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                }
            }
            @Override public void surfaceChanged(SurfaceHolder h, int f, int w, int h1) {}
            @Override public void surfaceDestroyed(SurfaceHolder h) { if(cameraDevice!=null) cameraDevice.close(); }
        });
    }

    private void openCamera(Surface surface) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cid = manager.getCameraIdList()[0];
            manager.openCamera(cid, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
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
                    } catch (Exception e) {}
                }
                @Override public void onDisconnected(CameraDevice c) { c.close(); }
                @Override public void onError(CameraDevice c, int e) { c.close(); }
            }, null);
        } catch (Exception e) {}
    }
}
