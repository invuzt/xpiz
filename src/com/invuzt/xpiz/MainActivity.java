package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout buttonContainer;
    private TextView logOutput, totalView;
    private int totalHarga = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);

        buttonContainer = new LinearLayout(this);
        root.addView(buttonContainer);

        totalView = new TextView(this);
        totalView.setText("TOTAL: Rp 0");
        totalView.setTextColor(Color.YELLOW);
        totalView.setTextSize(25);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        logOutput = new TextView(this);
        logOutput.setTextColor(Color.GREEN);
        logOutput.setTypeface(Typeface.MONOSPACE);
        root.addView(logOutput);

        EditText input = new EditText(this);
        input.setHint("Ketik sesuatu...");
        root.addView(input);

        input.setOnEditorActionListener((v, actionId, event) -> {
            String res = predictBestButton(input.getText().toString());
            if(res.startsWith("AUTO_BTN")) {
                String nama = res.split("\\|")[1];
                addButton(nama);
            } else {
                logOutput.append("\n> " + res);
            }
            input.setText("");
            return true;
        });

        setContentView(root);
    }

    private void addButton(String nama) {
        Button b = new Button(this);
        b.setText(nama);
        b.setOnClickListener(v -> {
            logOutput.append("\n[Shortcut] Memproses " + nama);
            // AI bisa di-expand di sini untuk aksi otomatis
        });
        buttonContainer.addView(b);
    }
}
