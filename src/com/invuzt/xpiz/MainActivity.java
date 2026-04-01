package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private TextView logView, trendView;
    private EditText inputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0A0A0A"));
        root.setPadding(40, 60, 40, 40);

        // Header Dashboard
        TextView header = new TextView(this);
        header.setText("ODFIZ PREDICTIVE ENGINE v3.0");
        header.setTextColor(Color.CYAN);
        header.setTextSize(18);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(header);

        // Layar Trend (Live Analysis)
        trendView = new TextView(this);
        trendView.setBackgroundColor(Color.parseColor("#1A1A1A"));
        trendView.setPadding(20, 20, 20, 20);
        trendView.setTextColor(Color.YELLOW);
        trendView.setText("TREND: WAITING FOR DATA...");
        root.addView(trendView);

        // Log Terminal
        logView = new TextView(this);
        logView.setTextColor(Color.parseColor("#00FF41"));
        logView.setTypeface(Typeface.MONOSPACE);
        root.addView(logView, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        inputField = new EditText(this);
        inputField.setHint("Masukkan angka stok/data...");
        inputField.setTextColor(Color.WHITE);
        inputField.setHintTextColor(Color.GRAY);
        root.addView(inputField);

        inputField.setOnEditorActionListener((v, aId, event) -> {
            String in = inputField.getText().toString();
            if(!in.isEmpty()){
                String res = predictBestButton(in);
                String[] p = res.split("\\|");
                trendView.setText(p[0]);
                logView.append("\n[IN]: " + in + " -> " + (p.length > 1 ? p[1] : "Calculating..."));
                inputField.setText("");
            }
            return true;
        });

        setContentView(root);
    }
}
