package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.Gravity;
import android.graphics.Color;
import java.io.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String processHugeFile(String path);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.BLACK);

        TextView tv = new TextView(this);
        tv.setText("ODFIZ DATA ENGINE\nReady for 10M Rows");
        tv.setTextColor(Color.GREEN);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);
        root.addView(tv);

        Button btn = new Button(this);
        btn.setText("HAJAR 10 JUTA DATA");
        btn.setPadding(20, 20, 20, 20);
        root.addView(btn);

        setContentView(root);

        btn.setOnClickListener(v -> {
            String path = getFilesDir() + "/data.txt";
            tv.setText("Rust sedang bekerja...");
            
            new Thread(() -> {
                String hasil = processHugeFile(path);
                runOnUiThread(() -> tv.setText(hasil));
            }).start();
        });

        // Buat file dummy 10 juta baris jika belum ada
        new Thread(() -> {
            File f = new File(getFilesDir(), "data.txt");
            if (!f.exists()) {
                try {
                    PrintWriter out = new PrintWriter(f);
                    for (int i = 1; i <= 10000000; i++) {
                        out.println(i);
                    }
                    out.close();
                } catch (Exception e) {}
            }
        }).start();
    }
}
