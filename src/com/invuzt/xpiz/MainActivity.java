package com.invuzt.xpiz;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String calculateNative(String expression, boolean b);

    private TextView tvDisplayExp, tvDisplayResult;
    private LinearLayout historyLayout;
    private String currentInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Menggunakan LinearLayout VERTICAL agar komponen berjejer rapi kebawah
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(20, 20, 20, 20);

        // --- 1. DISPLAY AREA ---
        LinearLayout displayArea = new LinearLayout(this);
        displayArea.setOrientation(LinearLayout.VERTICAL);
        displayArea.setPadding(30, 60, 30, 40);
        
        tvDisplayResult = new TextView(this);
        tvDisplayResult.setTextSize(55);
        tvDisplayResult.setTextColor(Color.BLACK);
        tvDisplayResult.setGravity(Gravity.END);
        tvDisplayResult.setTypeface(null, Typeface.BOLD);
        tvDisplayResult.setText("0");
        // Klik hasil untuk salin
        tvDisplayResult.setOnClickListener(v -> copyToClipboard(tvDisplayResult.getText().toString()));

        tvDisplayExp = new TextView(this);
        tvDisplayExp.setTextSize(22);
        tvDisplayExp.setTextColor(Color.GRAY);
        tvDisplayExp.setGravity(Gravity.END);
        tvDisplayExp.setText(" ");

        displayArea.addView(tvDisplayResult);
        displayArea.addView(tvDisplayExp);
        root.addView(displayArea);

        // --- 2. TOMBOL GRID ---
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        
        String[] buttons = {
            "C", "(", ")", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "DEL", "="
        };

        // Hitung ukuran tombol supaya pas di layar
        int screenWidth = getResources().getDisplayMetrics().widthPixels - 40;
        int btnSize = screenWidth / 4;

        for (String text : buttons) {
            Button b = new Button(this);
            b.setText(text);
            b.setTextSize(24);
            b.setTransformationMethod(null);
            
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = btnSize - 10;
            p.height = (int)((btnSize - 10) * 1.1);
            p.setMargins(5, 5, 5, 5);
            b.setLayoutParams(p);

            // Warna Tombol
            if (text.equals("=")) {
                b.setBackgroundColor(Color.parseColor("#4CAF50"));
                b.setTextColor(Color.WHITE);
            } else if (text.equals("C")) {
                b.setBackgroundColor(Color.parseColor("#F44336"));
                b.setTextColor(Color.WHITE);
            } else {
                b.setBackgroundColor(Color.parseColor("#EEEEEE"));
                b.setTextColor(Color.BLACK);
            }

            b.setOnClickListener(v -> onBtnClick(text));
            grid.addView(b);
        }
        root.addView(grid);

        // --- 3. RIWAYAT ---
        TextView label = new TextView(this);
        label.setText("\nRIWAYAT");
        label.setPadding(10, 20, 10, 10);
        label.setTypeface(null, Typeface.BOLD);
        root.addView(label);

        ScrollView scroll = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(-1, -1);
        scroll.setLayoutParams(scrollParams);

        historyLayout = new LinearLayout(this);
        historyLayout.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(historyLayout);
        root.addView(scroll);

        setContentView(root);
    }

    private void onBtnClick(String text) {
        if (text.equals("C")) {
            currentInput = "";
            tvDisplayExp.setText(" ");
            tvDisplayResult.setText("0");
        } else if (text.equals("DEL")) {
            if (currentInput.length() > 0) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            }
        } else if (text.equals("=")) {
            String res = tvDisplayResult.getText().toString();
            if (!res.isEmpty() && !res.equals("0") && !currentInput.isEmpty()) {
                addToHistory(currentInput + " = " + res);
                currentInput = res;
            }
        } else {
            currentInput += text;
        }
        
        String displayShow = currentInput.replace("*", "×").replace("/", "÷");
        tvDisplayExp.setText(displayShow.isEmpty() ? " " : displayShow);
        
        // Panggil Rust
        String r = calculateNative(currentInput, false);
        if (!r.isEmpty()) {
            tvDisplayResult.setText(r);
        }
    }

    private void addToHistory(String item) {
        TextView h = new TextView(this);
        h.setText(item);
        h.setTextSize(16);
        h.setPadding(10, 15, 10, 15);
        h.setOnClickListener(v -> copyToClipboard(item.split("=")[1].trim()));
        historyLayout.addView(h, 0);
    }

    private void copyToClipboard(String t) {
        if (t.equals("0") || t.isEmpty()) return;
        ClipboardManager cb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cb.setPrimaryClip(ClipData.newPlainText("res", t));
        Toast.makeText(this, "Berhasil disalin: " + t, Toast.LENGTH_SHORT).show();
    }
}
