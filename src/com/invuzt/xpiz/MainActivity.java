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

    private LinearLayout container;
    private TextView totalView, log;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(20, 20, 20, 20);

        // Barisan Tombol
        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        container = new LinearLayout(this);
        hScroll.addView(container);
        root.addView(hScroll);

        totalView = new TextView(this);
        totalView.setText("TOTAL: 0");
        totalView.setTextColor(Color.YELLOW);
        totalView.setTextSize(35);
        totalView.setGravity(Gravity.CENTER);
        totalView.setPadding(0, 50, 0, 50);
        root.addView(totalView);

        log = new TextView(this);
        log.setTextColor(Color.GREEN);
        log.setTypeface(Typeface.MONOSPACE);
        ScrollView vScroll = new ScrollView(this);
        vScroll.addView(log);
        root.addView(vScroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        // INPUT YANG SUDAH DIKUNCI ENTER-NYA
        EditText input = new EditText(this);
        input.setHint("Ketik Nama : Harga lalu Enter");
        input.setSingleLine(true); // INI BIAR GAK TURUN KE BAWAH
        input.setImeOptions(EditorInfo.IME_ACTION_SEND); // INI BIAR ENTER JADI TOMBOL KIRIM
        input.setTextColor(Color.WHITE);
        root.addView(input);

        input.setOnEditorActionListener((v, actionId, event) -> {
            // Cek apakah user pencet Enter atau tombol Send di keyboard
            if (actionId == EditorInfo.IME_ACTION_SEND || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                
                String txt = input.getText().toString();
                if(!txt.isEmpty()){
                    String res = predictBestButton(txt);
                    processOutput(res);
                    input.setText("");
                }
                return true;
            }
            return false;
        });
        setContentView(root);
    }

    private void processOutput(String res) {
        if(res.startsWith("NEW_BTN")) {
            String[] p = res.split("\\|");
            makeBtn(p[1], Integer.parseInt(p[2]));
        } else if(res.startsWith("CASH")) {
            int bayar = (int)Float.parseFloat(res.split("\\|")[1]);
            log.append("\n[BAYAR] " + bayar + " | KEMBALI: " + (bayar - total));
        } else {
            log.append("\n> " + res);
        }
    }

    private void makeBtn(String n, int h) {
        Button b = new Button(this);
        b.setText(n + "\n" + h);
        b.setOnClickListener(v -> {
            total += h;
            totalView.setText("TOTAL: " + total);
            log.append("\n+ " + n + " (" + h + ")");
        });
        container.addView(b);
    }
}
