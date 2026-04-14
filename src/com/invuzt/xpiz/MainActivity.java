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
import android.widget.*;
import java.util.ArrayList;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }

    private native String calculateNative(String expression, boolean isDegree);

    private TextView tvDisplay, tvResult;
    private LinearLayout historyLayout;
    private String currentInput = "";
    private boolean isDegree = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F1F3F4"));

        // --- DISPLAY AREA ---
        LinearLayout displayArea = new LinearLayout(this);
        displayArea.setOrientation(LinearLayout.VERTICAL);
        displayArea.setPadding(40, 60, 40, 40);
        
        tvDisplay = new TextView(this);
        tvDisplay.setTextSize(32);
        tvDisplay.setTextColor(Color.BLACK);
        tvDisplay.setGravity(Gravity.END);
        tvDisplay.setText("0");

        tvResult = new TextView(this);
        tvResult.setTextSize(20);
        tvResult.setTextColor(Color.GRAY);
        tvResult.setGravity(Gravity.END);
        tvResult.setOnClickListener(v -> copyToClipboard(tvResult.getText().toString()));

        displayArea.addView(tvDisplay);
        displayArea.addView(tvResult);
        root.addView(displayArea);

        // --- MODE & HISTORY TOGGLE ---
        Button btnMode = new Button(this);
        btnMode.setText("DEG");
        btnMode.setOnClickListener(v -> {
            isDegree = !isDegree;
            btnMode.setText(isDegree ? "DEG" : "RAD");
            updateCalculation();
        });
        root.addView(btnMode);

        // --- BUTTON GRID ---
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        String[] buttons = {
            "C", "(", ")", "/",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "π", "=",
            "sin", "cos", "tan", "sqrt"
        };

        for (String text : buttons) {
            Button b = new Button(this);
            b.setText(text);
            b.setOnClickListener(v -> onButtonClick(text));
            grid.addView(b);
        }
        root.addView(grid);

        // --- HISTORY VIEW ---
        TextView histHeader = new TextView(this);
        histHeader.setText("\n RIWAYAT (Klik untuk salin)");
        histHeader.setPadding(20, 10, 10, 10);
        root.addView(histHeader);

        historyLayout = new LinearLayout(this);
        historyLayout.setOrientation(LinearLayout.VERTICAL);
        root.addView(historyLayout);

        setContentView(root);
    }

    private void onButtonClick(String text) {
        if (text.equals("C")) {
            currentInput = "";
            tvResult.setText("");
        } else if (text.equals("=")) {
            String res = tvResult.getText().toString();
            if (!res.isEmpty()) {
                addToHistory(currentInput + " = " + res);
                currentInput = res;
            }
        } else if (text.equals("sin") || text.equals("cos") || text.equals("tan") || text.equals("sqrt")) {
            currentInput += text + "(";
        } else {
            currentInput += text.replace("×", "*");
        }
        
        tvDisplay.setText(currentInput.isEmpty() ? "0" : currentInput);
        updateCalculation();
    }

    private void updateCalculation() {
        if (currentInput.isEmpty()) return;
        try {
            String res = calculateNative(currentInput, isDegree);
            tvResult.setText(res);
        } catch (Exception e) {
            tvResult.setText("");
        }
    }

    private void addToHistory(String item) {
        TextView tv = new TextView(this);
        tv.setText(item);
        tv.setPadding(20, 10, 20, 10);
        tv.setOnClickListener(v -> copyToClipboard(item.split("=")[1].trim()));
        historyLayout.addView(tv, 0);
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("calc_res", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied: " + text, Toast.LENGTH_SHORT).show();
    }
}
