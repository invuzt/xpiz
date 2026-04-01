package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.view.inputmethod.EditorInfo;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout buttonContainer;
    private TextView logOutput, totalView;
    private int totalHarga = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Layout Utama
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(30, 30, 30, 30);

        // 1. AREA TOMBOL KASIR (Horizontal Scroll biar gak penuh)
        HorizontalScrollView scrollTombol = new HorizontalScrollView(this);
        buttonContainer = new LinearLayout(this);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        scrollTombol.addView(buttonContainer);
        root.addView(scrollTombol);

        // 2. DISPLAY TOTAL (Gede & Kuning)
        totalView = new TextView(this);
        totalView.setText("TOTAL: Rp 0");
        totalView.setTextColor(Color.YELLOW);
        totalView.setTextSize(30);
        totalView.setGravity(Gravity.CENTER);
        totalView.setPadding(0, 40, 0, 40);
        root.addView(totalView);

        // 3. LOG TERMINAL (Scrollable)
        logOutput = new TextView(this);
        logOutput.setTextColor(Color.GREEN);
        logOutput.setTypeface(Typeface.MONOSPACE);
        logOutput.setTextSize(12);
        ScrollView logScroll = new ScrollView(this);
        logScroll.addView(logOutput);
        root.addView(logScroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        // 4. INPUT PERINTAH
        EditText input = new EditText(this);
        input.setHint("add btn Dimsum : 12000");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.GRAY);
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        root.addView(input);

        input.setOnEditorActionListener((v, actionId, event) -> {
            String txt = input.getText().toString();
            if(!txt.isEmpty()){
                String res = predictBestButton(txt);
                processCommand(res);
                input.setText("");
            }
            return true;
        });

        setContentView(root);
    }

    private void processCommand(String res) {
        if (res.startsWith("CREATE_BTN")) {
            String[] p = res.split("\\|");
            if(p.length == 3) {
                addButton(p[1], Integer.parseInt(p[2]));
            }
        } else {
            logOutput.append("\n> " + res);
        }
    }

    private void addButton(String nama, int harga) {
        Button b = new Button(this);
        b.setText(nama + "\n" + harga);
        b.setAllCaps(false);
        b.setBackgroundColor(Color.DKGRAY);
        b.setTextColor(Color.WHITE);
        
        // Margin antar tombol
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        params.setMargins(10, 10, 10, 10);
        b.setLayoutParams(params);

        b.setOnClickListener(v -> {
            totalHarga += harga;
            totalView.setText("TOTAL: Rp " + totalHarga);
            logOutput.append("\n[+] " + nama + " (Rp" + harga + ")");
        });
        buttonContainer.addView(b);
    }
}
