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

    private LinearLayout productContainer;
    private TextView totalView, log;
    private int totalBelanja = 0;
    private HashMap<String, Button> productButtons = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#121212"));
        root.setPadding(25, 25, 25, 25);

        // --- 1. AREA PRODUK MODULAR (Scrolling Horizontal) ---
        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        productContainer = new LinearLayout(this);
        hScroll.addView(productContainer);
        root.addView(hScroll);

        // --- 2. DISPLAY TOTAL HARGA ---
        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextColor(Color.YELLOW);
        totalView.setTextSize(45);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        // --- 3. TOMBOL PERMANEN (PRINT & RESET) ---
        LinearLayout actionArea = new LinearLayout(this);
        actionArea.setGravity(Gravity.CENTER);
        
        Button btnPrint = new Button(this);
        btnPrint.setText("PRINT STRUK");
        btnPrint.setBackgroundColor(Color.parseColor("#2E7D32")); // Hijau
        btnPrint.setTextColor(Color.WHITE);
        btnPrint.setOnClickListener(v -> cetakStruk());
        
        Button btnReset = new Button(this);
        btnReset.setText("RESET");
        btnReset.setBackgroundColor(Color.RED);
        btnReset.setTextColor(Color.WHITE);
        btnReset.setOnClickListener(v -> resetTransaksi());

        actionArea.addView(btnPrint);
        actionArea.addView(btnReset);
        root.addView(actionArea);

        // --- 4. LOG TRANSAKSI ---
        log = new TextView(this);
        log.setTextColor(Color.GREEN);
        log.setTypeface(Typeface.MONOSPACE);
        ScrollView vScroll = new ScrollView(this);
        vScroll.addView(log);
        root.addView(vScroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        // --- 5. INPUT COMMAND (Tambah Produk / Masukkan Uang Bayar) ---
        EditText input = new EditText(this);
        input.setHint("Ketik 'Dimsum : 15000' atau '50000'");
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setTextColor(Color.WHITE);
        root.addView(input);

        input.setOnEditorActionListener((v, id, event) -> {
            String txt = input.getText().toString();
            if(!txt.isEmpty()) {
                String res = predictBestButton(txt);
                handleAiResponse(res);
                input.setText("");
            }
            return true;
        });

        setContentView(root);
    }

    private void handleAiResponse(String res) {
        String[] p = res.split("\\|");
        if (p[0].equals("ADD")) {
            addProductButton(p[1], Integer.parseInt(p[2]));
        } else if (p[0].equals("DEL")) {
            removeProductButton(p[1]);
        } else if (p[0].equals("CALC_CHANGE")) {
            int bayar = (int)Float.parseFloat(p[1]);
            int kembali = bayar - totalBelanja;
            log.append("\n[KASIR] Bayar: " + bayar + " | KEMBALI: " + kembali);
            if (kembali < 0) Toast.makeText(this, "Uang Kurang!", Toast.LENGTH_SHORT).show();
        } else {
            log.append("\n> " + res);
        }
    }

    private void addProductButton(String n, int h) {
        if (productButtons.containsKey(n)) return;
        Button b = new Button(this);
        b.setText(n + "\n" + h);
        b.setOnClickListener(v -> {
            totalBelanja += h;
            totalView.setText("Rp " + totalBelanja);
            log.append("\n+ " + n);
        });
        productContainer.addView(b);
        productButtons.put(n, b);
    }

    private void removeProductButton(String n) {
        if (productButtons.containsKey(n)) {
            productContainer.removeView(productButtons.get(n));
            productButtons.remove(n);
        }
    }

    private void cetakStruk() {
        log.append("\n\n--- ODFIZ PRINT OUT ---");
        log.append("\nTotal Akhir: Rp " + totalBelanja);
        log.append("\nStok Berkurang Otomatis.");
        log.append("\n-----------------------\n");
        resetTransaksi();
    }

    private void resetTransaksi() {
        totalBelanja = 0;
        totalView.setText("Rp 0");
        log.append("\n[RESET] Siap melayani pelanggan baru.");
    }
}
