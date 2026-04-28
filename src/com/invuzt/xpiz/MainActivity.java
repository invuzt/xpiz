package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String calculateNative(String expression, boolean b);

    private TextView tvDisplayExp, tvDisplayResult;
    private String currentInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(20, 20, 20, 20);

        // DISPLAY AREA
        LinearLayout displayArea = new LinearLayout(this);
        displayArea.setOrientation(LinearLayout.VERTICAL);
        displayArea.setPadding(30, 80, 30, 60);

        tvDisplayResult = new TextView(this);
        tvDisplayResult.setTextSize(60);
        tvDisplayResult.setTextColor(Color.BLACK);
        tvDisplayResult.setGravity(Gravity.END);
        tvDisplayResult.setTypeface(null, Typeface.BOLD);
        tvDisplayResult.setText("0");

        tvDisplayExp = new TextView(this);
        tvDisplayExp.setTextSize(24);
        tvDisplayExp.setTextColor(Color.GRAY);
        tvDisplayExp.setGravity(Gravity.END);
        tvDisplayExp.setText(" ");

        displayArea.addView(tvDisplayResult);
        displayArea.addView(tvDisplayExp);
        root.addView(displayArea);

        // GRID TOMBOL
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);

        String[] buttons = {
            "C", "(", ")", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "DEL", "="
        };

        int screenWidth = getResources().getDisplayMetrics().widthPixels - 40;
        int btnSize = screenWidth / 4;

        for (String text : buttons) {
            Button b = new Button(this);
            b.setText(text);
            b.setTextSize(22);
            b.setTransformationMethod(null);

            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = btnSize - 10;
            p.height = btnSize;
            p.setMargins(5, 5, 5, 5);
            b.setLayoutParams(p);

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
            if (!res.isEmpty() && !res.equals("0")) {
                currentInput = res;
            }
        } else {
            currentInput += text;
        }

        String displayShow = currentInput.replace("*", "×").replace("/", "÷");
        tvDisplayExp.setText(displayShow.isEmpty() ? " " : displayShow);

        String r = calculateNative(currentInput, false);
        if (!r.isEmpty()) tvDisplayResult.setText(r);
    }
}
