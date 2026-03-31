package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.Gravity;
import android.graphics.Color;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(int id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40, 40, 40, 40);

        TextView aiStatus = new TextView(this);
        aiStatus.setText("ODFIZ PREDICTIVE UI");
        aiStatus.setTextSize(20);
        aiStatus.setPadding(0, 0, 0, 50);
        root.addView(aiStatus);

        // Tombol Simulasi Produk
        String[] items = {"Pilih: KOPI", "Pilih: SABUN", "Pilih: STOK"};
        for (int i = 0; i < items.length; i++) {
            final int id = i + 1;
            Button b = new Button(this);
            b.setText(items[i]);
            b.setOnClickListener(v -> {
                String suggestion = predictBestButton(id);
                aiStatus.setText(suggestion);
                Toast.makeText(this, "AI Belajar dari klik Anda...", Toast.LENGTH_SHORT).show();
            });
            root.addView(b);
        }

        setContentView(root);
    }
}
