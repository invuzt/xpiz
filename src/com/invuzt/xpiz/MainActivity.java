package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.Surface;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.graphics.Color;
import android.widget.TextView;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("hello");
    }

    private native void startCameraPreview(Surface surface);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
            return;
        }

        // Buat Layout untuk menampung kamera
        FrameLayout layout = new FrameLayout(this);
        SurfaceView surfaceView = new SurfaceView(this);
        layout.addView(surfaceView);

        // Tambahkan indikator teks di atas kamera
        TextView hint = new TextView(this);
        hint.setText("xpiz Camera Engine: Active");
        hint.setTextColor(Color.GREEN);
        layout.addView(hint);

        setContentView(layout);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // KIRIM KANVAS KE RUST!
                startCameraPreview(holder.getSurface());
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate();
        }
    }
}
