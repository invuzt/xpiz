package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import java.util.HashMap;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout productContainer, payActionContainer;
    private TextView totalView, log;
    private int totalBelanja = 0;
    private HashMap<String, Button> productButtons = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#121212"));

        // 1. Tombol Produk
        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        productContainer = new LinearLayout(this);
        hScroll.addView(productContainer);
        root.addView(hScroll);

        // 2. Total
        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextColor(Color.YELLOW);
        totalView.setTextSize(45);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        // 3. Tombol Bayar Prediktif (AI Area)
        payActionContainer = new LinearLayout(this);
        payActionContainer.setGravity(Gravity.CENTER);
        root.addView(payActionContainer);

        // 4. Log/Struk
        log = new TextView(this);
        log.setTextColor(Color.GREEN);
        log.setTypeface(Typeface.MONOSPACE);
        ScrollView vScroll = new ScrollView(this);
        vScroll.addView(log);
        root.addView(vScroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        // 5. Input Manual
        EditText input = new EditText(this);
        input.setHint("Tambah Produk: 'Nama : Harga'");
        input.setTextColor(Color.WHITE);
        root.addView(input);

        input.setOnEditorActionListener((v, id, event) -> {
            String res = predictBestButton(input.getText().toString());
            if(res.startsWith("ADD")) {
                String[] p = res.split("\\|");
                addProductButton(p[1], Integer.parseInt(p[2]));
            }
            input.setText("");
            return true;
        });
        setContentView(root);
    }

    private void addProductButton(String n, int h) {
        Button b = new Button(this);
        b.setText(n + "\n" + h);
        
        // Klik Biasa = TAMBAH
        b.setOnClickListener(v -> {
            totalBelanja += h;
            updateUI();
        });

        // Klik Tahan = KURANGI (Mencegah Salah Klik)
        b.setOnLongClickListener(v -> {
            if(totalBelanja >= h) {
                totalBelanja -= h;
                updateUI();
                Toast.makeText(this, n + " dikurangi", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        productContainer.addView(b);
        productButtons.put(n, b);
    }

    private void updateUI() {
        totalView.setText("Rp " + totalBelanja);
        // Minta AI tebak nominal uang bayar
        String suggestions = predictBestButton("predict_pay|" + totalBelanja);
        updatePayButtons(suggestions);
    }

    private void updatePayButtons(String sug) {
        payActionContainer.removeAllViews();
        if(sug.startsWith("SUGGEST")) {
            String[] p = sug.split("\\|");
            for(int i=1; i<p.length; i++) {
                final int nominal = (int)Float.parseFloat(p[i]);
                Button b = new Button(this);
                b.setText("BAYAR " + nominal);
                b.setBackgroundColor(Color.BLUE);
                b.setTextColor(Color.WHITE);
                b.setOnClickListener(v -> finalizeTransaction(nominal));
                payActionContainer.addView(b);
            }
        }
    }

    private void finalizeTransaction(int bayar) {
        int kembali = bayar - totalBelanja;
        log.setText(""); // Bersihkan log untuk struk baru
        log.append("\n=== ODFIZ STRUK (PDF READY) ===");
        log.append("\nItem Belanja Tercatat...");
        log.append("\nTOTAL    : Rp " + totalBelanja);
        log.append("\nBAYAR    : Rp " + bayar);
        log.append("\nKEMBALI  : Rp " + kembali);
        log.append("\n===============================");
        log.append("\nTerima Kasih!");
        
        totalBelanja = 0;
        totalView.setText("Rp 0");
        payActionContainer.removeAllViews();
    }
}
