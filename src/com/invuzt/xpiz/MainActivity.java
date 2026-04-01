package com.invuzt.xpiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.view.inputmethod.EditorInfo;
import android.text.InputType;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout productContainer, pageTerminal, pageLaporan;
    private TextView totalView, aiNotif, txtStruk, txtRekap;
    private int totalBelanja = 0, omset = 0, txCount = 0;
    private String currentReceipt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Odfiz POS v2.6");
        toolbar.setBackgroundColor(Color.parseColor("#212121"));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getMenu().add(0, 1, 0, "KASIR");
        toolbar.getMenu().add(0, 2, 0, "LAPORAN");
        
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == 1) showPage(pageTerminal);
            if(item.getItemId() == 2) { updateLaporanText(); showPage(pageLaporan); }
            return true;
        });
        root.addView(toolbar);

        initTerminalPage();
        initLaporanPage();
        
        root.addView(pageTerminal);
        root.addView(pageLaporan);

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

        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextSize(50);
        totalView.setGravity(Gravity.CENTER);
        pageTerminal.addView(totalView);

        txtStruk = new TextView(this);
        txtStruk.setTypeface(Typeface.MONOSPACE);
        txtStruk.setBackgroundColor(Color.parseColor("#F0F0F0"));
        txtStruk.setPadding(30,30,30,30);
        txtStruk.setHint("Preview Struk (Klik untuk Print PDF)");
        txtStruk.setOnClickListener(v -> saveAsPdf());
        pageTerminal.addView(txtStruk, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        EditText input = new EditText(this);
        input.setHint("Ketik Nama:Harga atau Bayar...");
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setOnEditorActionListener((v, actionId, event) -> {
            String raw = input.getText().toString();
            if(!raw.isEmpty()) {
                handleAiResult(predictBestButton(raw));
                input.setText(""); 
            }
            return true;
        });
        pageTerminal.addView(input);
    }

    private void handleAiResult(String res) {
        String[] p = res.split("\\|");
        if(p[0].equals("ADD")) {
            totalBelanja += Integer.parseInt(p[2]);
            updateTotal();
        } else if(p[0].equals("PAY")) {
            finalizeTransaction(Integer.parseInt(p[1]));
        }
    }

    private void finalizeTransaction(int bayar) {
        int kembali = bayar - totalBelanja;
        new AlertDialog.Builder(this).setTitle("KEMBALIAN").setMessage("Rp " + kembali).show();
        currentReceipt = "ODFIZ POS\nTOTAL: " + totalBelanja + "\nKEMBALI: " + kembali;
        txtStruk.setText(currentReceipt);
        omset += totalBelanja; txCount++; totalBelanja = 0; updateTotal();
    }

    private void initLaporanPage() {
        pageLaporan = new LinearLayout(this);
        pageLaporan.setOrientation(LinearLayout.VERTICAL);
        txtRekap = new TextView(this);
        txtRekap.setTextSize(20);
        pageLaporan.addView(txtRekap);
    }

    private void updateLaporanText() {
        txtRekap.setText("LAPORAN\n\nOmset: Rp " + omset + "\nNota: " + txCount);
    }

    private void showPage(View p) {
        pageTerminal.setVisibility(View.GONE);
        pageLaporan.setVisibility(View.GONE);
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
        canvas.drawText(currentReceipt, 20, 40, p);
        doc.finishPage(page);
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Odfiz_Struk.pdf");
            doc.writeTo(new FileOutputStream(f));
            Toast.makeText(this, "PDF Berhasil!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {}
        doc.close();
    }
}
