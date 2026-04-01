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
import java.text.SimpleDateFormat;

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
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        // --- TOOLBAR CUSTOM ---
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Odfiz POS");
        toolbar.setBackgroundColor(Color.parseColor("#212121"));
        toolbar.setTitleTextColor(Color.WHITE);
        
        // Buat Menu secara Dinamis (Tanpa XML agar tidak error)
        toolbar.getMenu().add(0, 1, 0, "KASIR");
        toolbar.getMenu().add(0, 2, 0, "LAPORAN");
        toolbar.getMenu().add(0, 3, 0, "TENTANG");
        
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == 1) showPage(pageTerminal);
            if(item.getItemId() == 2) {
                updateLaporanText();
                showPage(pageLaporan);
            }
            if(item.getItemId() == 3) showPage(pageAbout);
            return true;
        });
        root.addView(toolbar);

        // --- PAGES ---
        initTerminalPage();
        initLaporanPage();
        initAboutPage();
        
        root.addView(pageTerminal);
        root.addView(pageLaporan);
        root.addView(pageAbout);

        showPage(pageTerminal);
        setContentView(root);
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
        totalView.setTextSize(45);
        totalView.setGravity(Gravity.CENTER);
        totalView.setTextColor(Color.BLACK);
        pageTerminal.addView(totalView);

        txtStruk = new TextView(this);
        txtStruk.setTypeface(Typeface.MONOSPACE);
        txtStruk.setBackgroundColor(Color.parseColor("#EEEEEE"));
        txtStruk.setPadding(20,20,20,20);
        txtStruk.setHint("Struk kosong (Klik untuk Simpan PDF)");
        txtStruk.setOnClickListener(v -> saveAsPdf());
        pageTerminal.addView(txtStruk, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        EditText input = new EditText(this);
        input.setHint("Ketik Nama:Harga atau Bayar");
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setOnEditorActionListener((v, id, event) -> {
            String res = predictBestButton(input.getText().toString());
            String[] p = res.split("\\|");
            if(p[0].equals("ADD")) addProduct(p[1], Integer.parseInt(p[2]));
            else if(p[0].equals("PAY")) finalizeTransaction(Integer.parseInt(p[1]));
            input.setText("");
            return true;
        });
        pageTerminal.addView(input);
    }

    private void addProduct(String n, int h) {
        Button b = new Button(this);
        b.setText(n + "\n" + h);
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

    private void finalizeTransaction(int bayar) {
        int kembali = bayar - totalBelanja;
        String date = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        
        currentReceipt = "   ODFIZ PONOROGO\n" +
                         "   Waktu: " + date + "\n" +
                         "--------------------\n" +
                         "TOTAL   : " + totalBelanja + "\n" +
                         "BAYAR   : " + bayar + "\n" +
                         "KEMBALI : " + kembali + "\n" +
                         "--------------------\n" +
                         "   SUWUN LUR!";
                         
        txtStruk.setText(currentReceipt);
        omset += totalBelanja;
        txCount++;
        totalBelanja = 0;
        updateTotal();
    }

    private void initLaporanPage() {
        pageLaporan = new LinearLayout(this);
        pageLaporan.setOrientation(LinearLayout.VERTICAL);
        pageLaporan.setPadding(30,30,30,30);
        txtRekap = new TextView(this);
        txtRekap.setTextSize(22);
        pageLaporan.addView(txtRekap);
    }

    private void updateLaporanText() {
        txtRekap.setText("REKAP HARI INI\n\nOmset: Rp " + omset + "\nTotal Nota: " + txCount);
    }

    private void initAboutPage() {
        pageAbout = new LinearLayout(this);
        pageAbout.setGravity(Gravity.CENTER);
        TextView t = new TextView(this);
        t.setText("Odfiz POS v2.1\nModular Terminal Interface");
        pageAbout.addView(t);
    }

    private void showPage(View p) {
        pageTerminal.setVisibility(View.GONE);
        pageLaporan.setVisibility(View.GONE);
        pageAbout.setVisibility(View.GONE);
        p.setVisibility(View.VISIBLE);
    }

    private void updateTotal() { totalView.setText("Rp " + totalBelanja); }

    private void saveAsPdf() {
        if(currentReceipt.isEmpty()) return;
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(300, 500, 1).create();
        PdfDocument.Page page = doc.startPage(pi);
        Canvas canvas = page.getCanvas();
        Paint p = new Paint();
        p.setTextSize(14);
        p.setTypeface(Typeface.MONOSPACE);
        
        int y = 40;
        for(String line : currentReceipt.split("\n")) {
            canvas.drawText(line, 20, y, p);
            y += 25;
        }
        
        doc.finishPage(page);
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Odfiz_Struk.pdf");
            doc.writeTo(new FileOutputStream(f));
            Toast.makeText(this, "PDF Tersimpan di Downloads", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {}
        doc.close();
    }
}
