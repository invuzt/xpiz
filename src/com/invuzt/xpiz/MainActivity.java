package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.view.inputmethod.EditorInfo;
import java.util.HashMap;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout productContainer, payActionContainer;
    private TextView totalView, log;
    private int totalBelanja = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#121212"));
        root.setPadding(20, 20, 20, 20);

        // 1. Area Tombol Produk
        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        productContainer = new LinearLayout(this);
        hScroll.addView(productContainer);
        root.addView(hScroll);

        // 2. Display Total
        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextColor(Color.YELLOW);
        totalView.setTextSize(40);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        // 3. Area Tombol Bayar AI
        payActionContainer = new LinearLayout(this);
        payActionContainer.setGravity(Gravity.CENTER);
        root.addView(payActionContainer);

        // 4. Log Struk
        log = new TextView(this);
        log.setTextColor(Color.GREEN);
        log.setTypeface(Typeface.MONOSPACE);
        ScrollView vScroll = new ScrollView(this);
        vScroll.addView(log);
        root.addView(vScroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        // 5. INPUT YANG SUDAH DI-FIX ENTER-NYA
        EditText input = new EditText(this);
        input.setHint("Nama : Harga (Lalu Enter)");
        input.setSingleLine(true); // INI BIAR GAK TURUN KE BAWAH
        input.setImeOptions(EditorInfo.IME_ACTION_SEND); // GANTI ENTER JADI KIRIM
        input.setTextColor(Color.WHITE);
        root.addView(input);

        // Listener buat dengerin tombol Enter/Send
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                
                String txt = input.getText().toString();
                if(!txt.isEmpty()){
                    String res = predictBestButton(txt);
                    handleRes(res);
                    input.setText(""); // Bersihkan input
                }
                return true; // Bilang ke sistem: "Gak usah turun ke bawah!"
            }
            return false;
        });

        setContentView(root);
    }

    private void handleRes(String res) {
        String[] p = res.split("\\|");
        if (p[0].equals("ADD")) {
            makeBtn(p[1], Integer.parseInt(p[2]));
        } else if (p[0].equals("SUGGEST")) {
            // Logika Bayar AI
        } else {
            log.append("\n> " + res);
        }
    }

    private void makeBtn(String n, int h) {
        Button b = new Button(this);
        b.setText(n + "\n" + h);
        b.setOnClickListener(v -> {
            totalBelanja += h;
            totalView.setText("Rp " + totalBelanja);
            // Minta AI tebak duit bayar
            String sug = predictBestButton("predict_pay|" + totalBelanja);
            showPayButtons(sug);
        });
        b.setOnLongClickListener(v -> {
            if(totalBelanja >= h) totalBelanja -= h;
            totalView.setText("Rp " + totalBelanja);
            return true;
        });
        productContainer.addView(b);
    }

    private void showPayButtons(String sug) {
        payActionContainer.removeAllViews();
        if(!sug.startsWith("SUGGEST")) return;
        String[] p = sug.split("\\|");
        for(int i=1; i<p.length; i++) {
            final int nominal = (int)Float.parseFloat(p[i]);
            Button b = new Button(this);
            b.setText("BAYAR " + nominal);
            b.setOnClickListener(v -> {
                log.setText("\n=== STRUK ===\nTOTAL: " + totalBelanja + "\nBAYAR: " + nominal + "\nKEMBALI: " + (nominal-totalBelanja));
                totalBelanja = 0;
                totalView.setText("Rp 0");
                payActionContainer.removeAllViews();
            });
            payActionContainer.addView(b);
        }
    }
}
