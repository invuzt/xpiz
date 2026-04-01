package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout container;
    private TextView totalView, log;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);

        container = new LinearLayout(this);
        root.addView(container);

        totalView = new TextView(this);
        totalView.setText("TOTAL: 0");
        totalView.setTextColor(Color.YELLOW);
        totalView.setTextSize(30);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        log = new TextView(this);
        log.setTextColor(Color.GREEN);
        root.addView(log);

        EditText input = new EditText(this);
        input.setHint("Ketik Nama : Harga lalu Enter");
        root.addView(input);

        input.setOnEditorActionListener((v, id, event) -> {
            String res = predictBestButton(input.getText().toString());
            if(res.startsWith("NEW_BTN")) {
                String[] p = res.split("\\|");
                makeBtn(p[1], Integer.parseInt(p[2]));
            } else if(res.startsWith("CASH")) {
                int bayar = (int)Float.parseFloat(res.split("\\|")[1]);
                log.append("\nBayar: " + bayar + " | Kembali: " + (bayar - total));
            }
            input.setText("");
            return true;
        });
        setContentView(root);
    }

    private void makeBtn(String n, int h) {
        Button b = new Button(this);
        b.setText(n + "\n" + h);
        b.setOnClickListener(v -> {
            total += h;
            totalView.setText("TOTAL: " + total);
            log.append("\n+ " + n);
        });
        container.addView(b);
    }
}
