package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.Color;

public class MainActivity extends Activity {
    static {
        // Nama harus sesuai dengan [lib] name di Cargo.toml
        System.loadLibrary("xpiz_rust");
    }

    private native String helloRust(String name);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setTextSize(24);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLACK);
        
        // Panggil fungsi Rust
        String pesanDariRust = helloRust("User");
        tv.setText(pesanDariRust);
        
        setContentView(tv);
    }
}
