package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import android.view.View;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String calculateNative(String expression, boolean b);

    private TextView tvDisplayExp, tvDisplayResult;
    private String currentInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Layout Utama ---
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F5F5F5"));

        // --- Display (Layar) ---
        setupDisplay(root);

        // --- Grid Tombol (Otomatis) ---
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setPadding(10, 10, 10, 10);

        // DAFTAR TOMBOL: Tambah di sini, otomatis muncul & terintegrasi Rust!
        String[] buttons = {
            "C", "(", ")", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "DEL", "="
        };

        for (String txt : buttons) {
            grid.addView(createButton(txt));
        }

        root.addView(grid);
        setContentView(root);
    }

    // Fungsi "Template" Tombol (Mirip tag <button> di HTML)
    private Button createButton(String text) {
        Button b = new Button(this);
        b.setText(text.replace("*","×").replace("/","÷"));
        b.setTextSize(22);
        
        // Styling ala CSS
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 180;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        b.setLayoutParams(params);

        if (text.equals("=")) b.setBackgroundColor(Color.parseColor("#4CAF50"));
        else if (text.equals("C")) b.setBackgroundColor(Color.parseColor("#F44336"));
        else b.setBackgroundColor(Color.WHITE);

        b.setOnClickListener(v -> handleAction(text));
        return b;
    }

    private void handleAction(String text) {
        if (text.equals("C")) currentInput = "";
        else if (text.equals("DEL")) {
            if (currentInput.length() > 0) currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else if (text.equals("=")) {
            currentInput = tvDisplayResult.getText().toString();
        } else {
            currentInput += text;
        }

        tvDisplayExp.setText(currentInput);
        String res = calculateNative(currentInput, false);
        if (!res.isEmpty()) tvDisplayResult.setText(res);
    }

    private void setupDisplay(LinearLayout root) {
        tvDisplayExp = new TextView(this);
        tvDisplayExp.setTextSize(20);
        tvDisplayExp.setPadding(40, 40, 40, 0);
        tvDisplayExp.setGravity(Gravity.END);
        
        tvDisplayResult = new TextView(this);
        tvDisplayResult.setTextSize(50);
        tvDisplayResult.setPadding(40, 0, 40, 40);
        tvDisplayResult.setTextColor(Color.BLACK);
        tvDisplayResult.setGravity(Gravity.END);
        tvDisplayResult.setText("0");

        root.addView(tvDisplayExp);
        root.addView(tvDisplayResult);
    }
}
