package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.Gravity;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("hello");
    }

    private native String stringFromRust();
    private native String openCameraRust();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setTextSize(20);
        tv.setTextColor(Color.GREEN);
        tv.setGravity(Gravity.CENTER);
        setContentView(tv);

        // 1. Cek apakah sudah ada izin kamera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 2. Jika belum, minta izin ke Satpam Android
            tv.setText("Meminta Izin Kamera...");
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            // 3. Jika sudah ada, langsung gas Rust!
            String msg = stringFromRust() + "\n" + openCameraRust();
            tv.setText(msg);
        }
    }

    // Callback setelah user klik "Allow" atau "Deny"
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate(); // Refresh activity kalau izin sudah didapat
        }
    }
}
