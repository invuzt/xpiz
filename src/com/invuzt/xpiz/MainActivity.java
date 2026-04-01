package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.view.inputmethod.EditorInfo;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout pageTerminal, pageLaporan, pageAbout, productContainer;
    private TextView totalView, txtStruk, txtRekap;
    private int totalBelanja = 0, omset = 0, txCount = 0;
    private String currentReceipt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Root Layout dengan Toolbar
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        // --- TOOLBAR ---
        setupToolbar(root);

        // --- CONTENT AREA (Pages) ---
        FrameLayout content = new FrameLayout(this);
        initTerminalPage();
        initLaporanPage();
        initAboutPage();
        
        content.addView(pageTerminal);
        content.addView(pageLaporan);
        content.addView(pageAbout);
        root.addView(content);

        showPage(pageTerminal);
        setContentView(root);
    }

    private void setupToolbar(LinearLayout root) {
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Odfiz POS");
        toolbar.setBackgroundColor(Color.parseColor("#212121"));
        toolbar.setTitleTextColor(Color.WHITE);
        
        // Menu Titik 3
        toolbar.inflateMenu(android.R.menu.search); // Pakai dummy dulu
        toolbar.getMenu().add(0, 1, 0, "Terminal");
        toolbar.getMenu().add(0, 2, 0, "Laporan");
        toolbar.getMenu().add(0, 3, 0, "About");
        
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == 1) showPage(pageTerminal);
            if(item.getItemId() == 2) showPage(pageLaporan);
            if(item.getItemId() == 3) showPage(pageAbout);
            return true;
        });
        root.addView(toolbar);
    }

    private void initTerminalPage() {
        pageTerminal = new LinearLayout(this);
        pageTerminal.setOrientation(LinearLayout.VERTICAL);
        pageTerminal.setPadding(20,20,20,20);

        HorizontalScrollView h = new HorizontalScrollView(this);
        productContainer = new LinearLayout(this);
        h.addView(productContainer);
        pageTerminal.addView(h);

        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextSize(40);
        totalView.setGravity(Gravity.CENTER);
        pageTerminal.addView(totalView);

        txtStruk = new TextView(this);
        txtStruk.setTypeface(Typeface.MONOSPACE);
        txtStruk.setBackgroundColor(Color.parseColor("#F5F5F5"));
        txtStruk.setHint("Struk akan muncul di sini (Klik untuk Print)");
        txtStruk.setOnClickListener(v -> savePdf());
        pageTerminal.addView(txtStruk, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        EditText input = new EditText(this);
        input.setHint("Input Nama:Harga atau Bayar");
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setOnEditorActionListener((v, id, event) -> {
            String res = predictBestButton(input.getText().toString());
            handleCmd(res);
            input.setText("");
            return true;
        });
        pageTerminal.addView(input);
    }

    private void handleCmd(String res) {
        String[] p = res.split("\\|");
        if(p[0].equals("ADD")) addProduct(p[1], Integer.parseInt(p[2]));
        else if(p[0].equals("PAY")) finalizePay(Integer.parseInt(p[1]));
    }

    private void addProduct(String n, int h) {
        Button b = new Button(this);
        b.setText(n);
        b.setOnClickListener(v -> {
            totalBelanja += h;
            updateTotal();
        });
        b.setOnLongClickListener(v -> {
            if(totalBelanja >= h) totalBelanja -= h;
            updateTotal();
            return true;
        });
        productContainer.addView(b);
    }

    private void finalizePay(int bayar) {
        currentReceipt = "   ODFIZ STRUK\nTotal: " + totalBelanja + "\nBayar: " + bayar + "\nKembali: " + (bayar-totalBelanja);
        txtStruk.setText(currentReceipt);
        omset += totalBelanja;
        txCount++;
        totalBelanja = 0;
        updateTotal();
        txtRekap.setText("Total Omset: Rp " + omset + "\nTotal Transaksi: " + txCount);
    }

    private void initLaporanPage() {
        pageLaporan = new LinearLayout(this);
        pageLaporan.setOrientation(LinearLayout.VERTICAL);
        txtRekap = new TextView(this);
        txtRekap.setTextSize(20);
        txtRekap.setText("Belum ada transaksi.");
        pageLaporan.addView(txtRekap);
    }

    private void initAboutPage() {
        pageAbout = new LinearLayout(this);
        pageAbout.setGravity(Gravity.CENTER);
        TextView t = new TextView(this);
        t.setText("Odfiz POS v2.0\nBy Developer Ponorogo");
        pageAbout.addView(t);
    }

    private void showPage(View p) {
        pageTerminal.setVisibility(View.GONE);
        pageLaporan.setVisibility(View.GONE);
        pageAbout.setVisibility(View.GONE);
        p.setVisibility(View.VISIBLE);
    }

    private void updateTotal() { totalView.setText("Rp " + totalBelanja); }

    private void savePdf() {
        if(currentReceipt.isEmpty()) return;
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(300, 500, 1).create();
        PdfDocument.Page page = doc.startPage(pi);
        page.getCanvas().drawText(currentReceipt, 10, 50, new Paint());
        doc.finishPage(page);
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "OdfizStruk.pdf");
            doc.writeTo(new FileOutputStream(f));
            Toast.makeText(this, "Struk PDF Berhasil Dibuat di Downloads!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {}
        doc.close();
    }
}
