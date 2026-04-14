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

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(20, 20, 20, 20);

        // --- DISPLAY ---
        LinearLayout displayArea = new LinearLayout(this);
        displayArea.setId(View.generateViewId());
        displayArea.setOrientation(LinearLayout.VERTICAL);
        displayArea.setPadding(30, 80, 30, 50);
        
        RelativeLayout.LayoutParams dp = new RelativeLayout.LayoutParams(-1, -2);
        dp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        displayArea.setLayoutParams(dp);

        tvDisplayResult = new TextView(this);
        tvDisplayResult.setTextSize(60);
        tvDisplayResult.setTextColor(Color.BLACK);
        tvDisplayResult.setGravity(Gravity.END);
        tvDisplayResult.setTypeface(null, Typeface.BOLD);
        tvDisplayResult.setText("0");
        tvDisplayResult.setOnClickListener(v -> copyToClipboard(tvDisplayResult.getText().toString()));

        tvDisplayExp = new TextView(this);
        tvDisplayExp.setTextSize(24);
        tvDisplayExp.setTextColor(Color.GRAY);
        tvDisplayExp.setGravity(Gravity.END);
        tvDisplayExp.setText(" ");

        displayArea.addView(tvDisplayResult);
        displayArea.addView(tvDisplayExp);
        root.addView(displayArea);

        // --- TOMBOL (GRID 4x5) ---
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        RelativeLayout.LayoutParams gp = new RelativeLayout.LayoutParams(-1, -2);
        gp.addRule(RelativeLayout.BELOW, displayArea.getId());
        grid.setLayoutParams(gp);

        String[] buttons = {
            "C", "(", ")", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "DEL", "="
        };

        int btnW = (getResources().getDisplayMetrics().widthPixels - 60) / 4;
        int btnH = (int)(btnW * 1.1);

        for (String text : buttons) {
            Button b = new Button(this);
            b.setText(text);
            b.setTextSize(26);
            b.setTransformationMethod(null);
            
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = btnW; p.height = btnH;
            p.setMargins(5, 5, 5, 5);
            b.setLayoutParams(p);

            if (text.equals("=")) b.setBackgroundColor(Color.parseColor("#4CAF50"));
            else if (text.equals("C")) b.setBackgroundColor(Color.parseColor("#F44336"));
            else b.setBackgroundColor(Color.parseColor("#EEEEEE"));
            
            if (text.equals("=")) b.setTextColor(Color.WHITE);
            else if (text.equals("C")) b.setTextColor(Color.WHITE);
            else b.setTextColor(Color.BLACK);

            b.setOnClickListener(v -> onBtnClick(text));
            grid.addView(b);
        }
        root.addView(grid);

        // --- RIWAYAT ---
        ScrollView scroll = new ScrollView(this);
        RelativeLayout.LayoutParams sp = new RelativeLayout.LayoutParams(-1, -1);
        sp.addRule(RelativeLayout.BELOW, grid.getId());
        sp.setMargins(0, 40, 0, 0);
        scroll.setLayoutParams(sp);

        historyLayout = new LinearLayout(this);
        historyLayout.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(historyLayout);
        root.addView(scroll);

        setContentView(root);
    }

    private void onBtnClick(String text) {
        if (text.equals("C")) currentInput = "";
        else if (text.equals("DEL")) {
            if (currentInput.length() > 0) currentInput = currentInput.substring(0, currentInput.length()-1);
        } else if (text.equals("=")) {
            String res = tvDisplayResult.getText().toString();
            if (!res.isEmpty() && !res.equals("0")) {
                addToHistory(currentInput + " = " + res);
                currentInput = res;
            }
        } else {
            currentInput += text;
        }
        
        tvDisplayExp.setText(currentInput.isEmpty() ? " " : currentInput);
        String r = calculateNative(currentInput, false);
        tvDisplayResult.setText(r.isEmpty() ? "0" : r);
    }

    private void addToHistory(String item) {
        TextView h = new TextView(this);
        h.setText(item);
        h.setPadding(10, 10, 10, 10);
        h.setOnClickListener(v -> copyToClipboard(item.split("=")[1].trim()));
        historyLayout.addView(h, 0);
    }

    private void copyToClipboard(String t) {
        ClipboardManager cb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cb.setPrimaryClip(ClipData.newPlainText("res", t));
        Toast.makeText(this, "Copied: " + t, Toast.LENGTH_SHORT).show();
    }
}
