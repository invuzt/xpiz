package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.Color;

public class MainActivity extends Activity {
    // Nama library sesuai dengan file .so (libhello.so)
    static { 
        try {
            System.loadLibrary("hello"); 
        } catch (UnsatisfiedLinkError e) {
            // Log error jika library tidak ditemukan
        }
    }

    private native String checkRustConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(40, 40, 40, 40);
        
        try {
            String status = checkRustConnection();
            tv.setText(status);
            tv.setTextColor(Color.GREEN);
        } catch (Exception e) {
            tv.setText("Koneksi Rust Gagal: \n" + e.getMessage());
            tv.setTextColor(Color.RED);
        }
        
        setContentView(tv);
    }
}
