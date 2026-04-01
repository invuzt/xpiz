package com.invuzt.xpiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent; // Kunci pindah halaman
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

    private LinearLayout pageTerminal, pageLaporan, pageAbout;
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
        toolbar.setTitle("Odfiz POS v2.5");
        toolbar.setBackgroundColor(Color.parseColor("#212121"));
        toolbar.setTitleTextColor(Color.WHITE);
        
        // Tambah Menu Node Designer
        toolbar.getMenu().add(0, 1, 0, "KASIR");
        toolbar.getMenu().add(0, 2, 0, "LAPORAN");
        toolbar.getMenu().add(0, 3, 0, "DESIGN LOGIC"); // Menu Baru
        toolbar.getMenu().add(0, 4, 0, "ABOUT");
        
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == 1) showPage(pageTerminal);
            if(item.getItemId() == 2) { updateLaporanText(); showPage(pageLaporan); }
            if(item.getItemId() == 3) {
                // PINDAH KE HALAMAN NODE
                Intent intent = new Intent(this, com.invuzt.logic.CanvasActivity.class);
                startActivity(intent);
            }
            if(item.getItemId() == 4) showPage(pageAbout);
            return true;
        });
        root.addView(toolbar);

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
        pageTerminal.addView(aiNotif);

        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextSize(50);
        totalView.setGravity(Gravity.CENTER);
        pageTerminal.addView(totalView);

        txtStruk = new TextView(this);
        txtStruk.setTypeface(Typeface.MONOSPACE);
        txtStruk.setBackgroundColor(Color.parseColor("#EEEEEE"));
        txtStruk.setPadding(20,20,20,20);
        txtStruk.setHint("Preview Struk...");
        pageTerminal.addView(txtStruk, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        EditText input = new EditText(this);
        input.setHint("Input Nama:Harga atau Bayar");
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setOnEditorActionListener((v, id, ev) -> {
            String res = predictBestButton(input.getText().toString());
            handleAi(res);
            input.setText("");
            return true;
        });
        pageTerminal.addView(input);
    }

    private void handleAi(String res) {
        String[] p = res.split("\\|");
        if(p[0].equals("ADD")) { totalBelanja += Integer.parseInt(p[2]); updateTotal(); }
        else if(p[0].equals("PAY")) finalizePay(Integer.parseInt(p[1]));
    }

    private void finalizePay(int b) {
        new AlertDialog.Builder(this).setMessage("Kembali: " + (b - totalBelanja)).show();
        currentReceipt = "TOTAL: " + totalBelanja;
        txtStruk.setText(currentReceipt);
        omset += totalBelanja; txCount++; totalBelanja = 0; updateTotal();
    }

    private void initLaporanPage() {
        pageLaporan = new LinearLayout(this);
        pageLaporan.setOrientation(LinearLayout.VERTICAL);
        txtRekap = new TextView(this);
        pageLaporan.addView(txtRekap);
    }

    private void updateLaporanText() { txtRekap.setText("Omset: " + omset); }

    private void initAboutPage() {
        pageAbout = new LinearLayout(this);
        TextView t = new TextView(this);
        t.setText("Odfiz POS Logic Builder");
        pageAbout.addView(t);
    }

    private void showPage(View p) {
        pageTerminal.setVisibility(View.GONE);
        pageLaporan.setVisibility(View.GONE);
        pageAbout.setVisibility(View.GONE);
        p.setVisibility(View.VISIBLE);
    }

    private void updateTotal() { totalView.setText("Rp " + totalBelanja); }
}
