package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    // Nama library sesuai dengan [lib] name di Cargo.toml
    static { System.loadLibrary("xpiz_core"); }
    private native String calculateNative(String expression);

    private TextView tvDisplayExp, tvDisplayResult;
    private String currentInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F5F5F5"));

        // Display
        tvDisplayExp = new TextView(this);
        tvDisplayExp.setTextSize(20);
        tvDisplayExp.setPadding(40, 80, 40, 0);
        tvDisplayExp.setGravity(Gravity.END);
        
        tvDisplayResult = new TextView(this);
        tvDisplayResult.setTextSize(50);
        tvDisplayResult.setPadding(40, 0, 40, 80);
        tvDisplayResult.setTextColor(Color.BLACK);
        tvDisplayResult.setGravity(Gravity.END);
        tvDisplayResult.setText("0");

        root.addView(tvDisplayExp);
        root.addView(tvDisplayResult);

        // Buttons
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        String[] buttons = {"C", "(", ")", "/", "7", "8", "9", "*", "4", "5", "6", "-", "1", "2", "3", "+", "0", ".", "DEL", "="};

        for (String txt : buttons) {
            Button b = new Button(this);
            b.setText(txt.replace("*","×").replace("/","÷"));
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = 0; p.height = 180;
            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            b.setLayoutParams(p);
            
            b.setOnClickListener(v -> {
                if (txt.equals("C")) currentInput = "";
                else if (txt.equals("DEL")) {
                    if (currentInput.length() > 0) currentInput = currentInput.substring(0, currentInput.length() - 1);
                } else if (txt.equals("=")) {
                    currentInput = tvDisplayResult.getText().toString();
                } else currentInput += txt;

                tvDisplayExp.setText(currentInput);
                String res = calculateNative(currentInput);
                if (!res.isEmpty()) tvDisplayResult.setText(res);
            });
            grid.addView(b);
        }
        root.addView(grid);
        setContentView(root);
    }
}
