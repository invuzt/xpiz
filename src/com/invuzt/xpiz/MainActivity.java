package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.view.Surface;
import android.widget.Toast;
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
        setContentView(sv);

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCameraProcess(sv.getHolder());
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    private void startCameraProcess(SurfaceHolder holder) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cid = manager.getCameraIdList()[0];
            manager.openCamera(cid, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    createPreview(holder);
                }
                @Override public void onDisconnected(CameraDevice c) { c.close(); }
                @Override public void onError(CameraDevice c, int e) { c.close(); }
            }, null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void createPreview(SurfaceHolder holder) {
        try {
            Surface surface = holder.getSurface();
            final CaptureRequest.Builder br = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            br.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(br.build(), null, null);
                        // Tes kirim data kecil ke Rust (Simulasi frame)
                        String hasil = analyzeFrame(new byte[1024], 32, 32);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, hasil, Toast.LENGTH_LONG).show());
                    } catch (Exception e) { e.printStackTrace(); }
                }
                @Override public void onConfigureFailed(CameraCaptureSession s) {}
            }, null);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
