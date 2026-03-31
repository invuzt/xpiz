package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.view.Surface;
import android.util.Size;
import android.util.Log;
import android.graphics.Matrix;
import android.graphics.RectF;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("hello");
    }

    private native String analyzeFrame(byte[] data, int width, int height);
    private CameraDevice cameraDevice;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new SurfaceView(this);
        surfaceView.getHolder().setKeepScreenOn(true);
        setContentView(surfaceView);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera(holder);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                }
            }
            @Override public void surfaceChanged(SurfaceHolder h, int f, int w, int h1) {
                configureTransform(w, h1);
            }
            @Override public void surfaceDestroyed(SurfaceHolder h) { if(cameraDevice != null) cameraDevice.close(); }
        });
    }

    private void openCamera(SurfaceHolder holder) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[0];
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    startPreview(holder.getSurface());
                }
                @Override public void onDisconnected(CameraDevice c) { c.close(); }
                @Override public void onError(CameraDevice c, int e) { c.close(); }
            }, null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startPreview(Surface surface) {
        try {
            final CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(builder.build(), null, null);
                        analyzeFrame(new byte[1], 1, 1);
                    } catch (Exception e) { e.printStackTrace(); }
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) {}
            }, null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // FUNGSI ANTI-PENYET: Mengatur rotasi dan skala gambar
    private void configureTransform(int viewWidth, int viewHeight) {
        if (surfaceView == null) return;
        Matrix matrix = new Matrix();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            // Jika miring, sesuaikan koordinat agar tidak gepeng
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        // Catatan: SurfaceView standar terbatas untuk transformasi Matrix.
        // Jika masih penyet, kita akan ganti ke TextureView di step berikutnya.
    }

    @Override
    public void onRequestPermissionsResult(int rc, String[] p, int[] gr) {
        if (rc == 101 && gr.length > 0 && gr[0] == PackageManager.PERMISSION_GRANTED) recreate();
    }
}
