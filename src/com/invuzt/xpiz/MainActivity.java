package com.invuzt.xpiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.view.inputmethod.EditorInfo;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout productContainer, pageTerminal, pageLaporan, pageAbout;
    private TextView totalView, aiNotif, txtStruk, txtRekap;
    private int totalBelanja = 0, omset = 0, txCount = 0;
    private String currentReceipt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        // --- TOOLBAR ---
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Odfiz POS v2.3");
        toolbar.setBackgroundColor(Color.parseColor("#212121"));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getMenu().add(0, 1, 0, "KASIR");
        toolbar.getMenu().add(0, 2, 0, "LAPORAN");
        toolbar.getMenu().add(0, 3, 0, "TENTANG");
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == 1) showPage(pageTerminal);
            if(item.getItemId() == 2) { updateLaporanText(); showPage(pageLaporan); }
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

        aiNotif = new TextView(this);
        aiNotif.setText("AI Odfiz: Standby");
        aiNotif.setTextColor(Color.BLUE);
        pageTerminal.addView(aiNotif);

        HorizontalScrollView h = new HorizontalScrollView(this);
        productContainer = new LinearLayout(this);
        h.addView(productContainer);
        pageTerminal.addView(h);

        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextSize(50);
        totalView.setGravity(Gravity.CENTER);
        totalView.setTextColor(Color.RED);
        pageTerminal.addView(totalView);

        txtStruk = new TextView(this);
        txtStruk.setTypeface(Typeface.MONOSPACE);
        txtStruk.setBackgroundColor(Color.parseColor("#F0F0F0"));
        txtStruk.setPadding(30,30,30,30);
        txtStruk.setHint("Preview Struk (Klik untuk Print)");
        txtStruk.setOnClickListener(v -> saveAsPdf());
        pageTerminal.addView(txtStruk, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        // --- INPUT YANG DIKUNCI MATI ---
        EditText input = new EditText(this);
        input.setHint("Ketik Nama:Harga atau Bayar...");
        input.setSingleLine(true); // Kunci 1: Baris tunggal
        input.setLines(1);         // Kunci 2: Paksa cuma 1 baris
        input.setMaxLines(1);      // Kunci 3: Maksimum 1 baris
        input.setInputType(InputType.TYPE_CLASS_TEXT); // Kunci 4: Teks biasa, bukan multi-line
        input.setImeOptions(EditorInfo.IME_ACTION_SEND); // Kunci 5: Ubah icon Enter jadi "Kirim"
        
        input.setOnEditorActionListener((v, actionId, event) -> {
            // Kita cegah aksi Enter bawaan (turun ke bawah)
            if (actionId == EditorInfo.IME_ACTION_SEND || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                
                String raw = input.getText().toString();
                if(!raw.isEmpty()) {
                    handleAiResult(predictBestButton(raw));
                    input.setText(""); 
                }
                return true; // Bilang ke Android: "Selesai, jangan tambah baris baru!"
            }
            return false;
        });
        pageTerminal.addView(input);
    }

    private void handleAiResult(String res) {
        String[] p = res.split("\\|");
        if(p[0].equals("ADD")) addProduct(p[1], Integer.parseInt(p[2]));
        else if(p[0].equals("PAY")) finalizeTransaction(Integer.parseInt(p[1]));
        else if(p[0].equals("AI_MSG")) aiNotif.setText("AI: " + p[1]);
        else if(p[0].equals("AI_WARN")) {
            aiNotif.setText("PERINGATAN: " + p[2]);
            aiNotif.setTextColor(Color.RED);
        }
    }

    private void addProduct(String n, int h) {
        Button b = new Button(this);
        b.setText(n + "\n" + h);
        b.setOnClickListener(v -> { 
            totalBelanja += h; 
            updateTotal(); 
            predictBestButton("jual " + n); 
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
        new AlertDialog.Builder(this)
            .setTitle("KEMBALIAN")
            .setMessage("Rp " + kembali)
            .setPositiveButton("OK", null)
            .show();

        String date = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        currentReceipt = "   ODFIZ POS\n" + date + "\n----------\nTOTAL: " + totalBelanja + "\nKEMBALI: " + kembali;
        txtStruk.setText(currentReceipt);
        
        omset += totalBelanja; txCount++; totalBelanja = 0; updateTotal();
    }

    private void initLaporanPage() {
        pageLaporan = new LinearLayout(this);
        pageLaporan.setOrientation(LinearLayout.VERTICAL);
        pageLaporan.setPadding(40,40,40,40);
        txtRekap = new TextView(this);
        txtRekap.setTextSize(24);
        pageLaporan.addView(txtRekap);
    }

    private void updateLaporanText() {
        txtRekap.setText("REKAP\n\nOmset: Rp " + omset + "\nNota: " + txCount);
    }

    private void initAboutPage() {
        pageAbout = new LinearLayout(this);
        pageAbout.setGravity(Gravity.CENTER);
        TextView t = new TextView(this);
        t.setText("Odfiz POS v2.3\nPonorogo");
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
            y += 28;
        }
        doc.finishPage(page);
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Odfiz_Bill.pdf");
            doc.writeTo(new FileOutputStream(f));
            Toast.makeText(this, "PDF Tersimpan!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {}
        doc.close();
    }
}
