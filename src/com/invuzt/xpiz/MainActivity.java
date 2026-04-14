package com.invuzt.xpiz;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }

    private native String calculateNative(String expression, boolean isDegree);

    private TextView tvDisplayExp, tvDisplayResult;
    private LinearLayout historyLayout;
    private String currentInput = "";
    private boolean isDegree = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ROOT: Menggunakan RelativeLayout agar Display dan Tombol terpisah rapi
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#F1F3F4"));
        root.setPadding(30, 30, 30, 30);

        // --- 1. DISPLAY AREA (Paling Atas) ---
        LinearLayout displayArea = new LinearLayout(this);
        displayArea.setId(View.generateViewId());
        displayArea.setOrientation(LinearLayout.VERTICAL);
        displayArea.setPadding(30, 50, 30, 30);
        displayArea.setBackgroundColor(Color.WHITE);
        
        RelativeLayout.LayoutParams displayParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT);
        displayParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        displayArea.setLayoutParams(displayParams);

        // Ekspresi kecil (misal: 3+2) - Pindah ke bawah
        tvDisplayExp = new TextView(this);
        tvDisplayExp.setTextSize(22);
        tvDisplayExp.setTextColor(Color.GRAY);
        tvDisplayExp.setGravity(Gravity.END);
        tvDisplayExp.setText(" ");
        tvDisplayExp.setPadding(0, 0, 0, 10);

        // Hasil Besar (misal: 5) - Pindah ke Atas
        tvDisplayResult = new TextView(this);
        tvDisplayResult.setTextSize(48); // Lebih besar
        tvDisplayResult.setTextColor(Color.BLACK);
        tvDisplayResult.setTypeface(null, Typeface.BOLD);
        tvDisplayResult.setGravity(Gravity.END);
        tvDisplayResult.setText("0");
        tvDisplayResult.setOnClickListener(v -> copyToClipboard(tvDisplayResult.getText().toString()));

        // Susun: Hasil Besar di atas Ekspresi Kecil
        displayArea.addView(tvDisplayResult);
        displayArea.addView(tvDisplayExp);
        root.addView(displayArea);


        // --- 2. LAYOUT TOMBOL (Bawah Display) ---
        LinearLayout buttonArea = new LinearLayout(this);
        buttonArea.setId(View.generateViewId());
        buttonArea.setOrientation(LinearLayout.VERTICAL);
        
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.BELOW, displayArea.getId()); // Tepat di bawah Display
        buttonParams.setMargins(0, 30, 0, 0); // Spasi kecil
        buttonArea.setLayoutParams(buttonParams);

        // Tombol Mode DEG/RAD (Memakai layout full width)
        Button btnMode = new Button(this);
        btnMode.setText("DEG");
        btnMode.setTransformationMethod(null); // Jangan Caps
        LinearLayout.LayoutParams modeParams = new LinearLayout.LayoutParams(-1, -2);
        modeParams.setMargins(0, 0, 0, 15);
        btnMode.setLayoutParams(modeParams);
        btnMode.setOnClickListener(v -> {
            isDegree = !isDegree;
            btnMode.setText(isDegree ? "DEG" : "RAD");
            updateCalculation();
        });
        buttonArea.addView(btnMode);

        // GRID TOMBOL UTAMA (Ditingkatkan ukurannya)
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        grid.setRowOrderPreserved(false);
        grid.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        String[] buttons = {
            "C", "(", ")", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "π", "=",
            "sin", "cos", "tan", "sqrt"
        };

        // Menghitung lebar layar untuk tombol yang presisi
        int screenWidth = getResources().getDisplayMetrics().widthPixels - 60; // Kurangi padding root
        int buttonSize = (screenWidth / 4) - 10; // Dibagi 4 kolom, dikurangi margin

        for (String text : buttons) {
            Button b = new Button(this);
            b.setText(text);
            b.setTextSize(22); // Teks tombol lebih besar
            b.setTransformationMethod(null); // Jangan Caps otomatis
            b.setPadding(0,0,0,0);
            b.setOnClickListener(v -> onButtonClick(text));
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = buttonSize;
            params.height = (int)(buttonSize * 0.9); // Tinggi sedikit lebih pendek dari lebar
            params.setMargins(5, 5, 5, 5); // Margin antar tombol
            b.setLayoutParams(params);
            
            // Warna tombol khusus
            if (text.equals("=") || text.equals("C")) {
                b.setBackgroundColor(Color.parseColor("#34A853")); // Hijau
                b.setTextColor(Color.WHITE);
            } else if ("÷×-+".contains(text)) {
                 b.setBackgroundColor(Color.parseColor("#DADCE0")); // Abu terang
                 b.setTextColor(Color.BLACK);
            } else {
                 b.setBackgroundColor(Color.parseColor("#F8F9FA")); // Putih bersih
                 b.setTextColor(Color.BLACK);
            }

            grid.addView(b);
        }
        buttonArea.addView(grid);
        root.addView(buttonArea);


        // --- 3. RIWAYAT (Di bawah tombol, Scrollable) ---
        TextView histHeader = new TextView(this);
        histHeader.setId(View.generateViewId());
        histHeader.setText("RIWAYAT (Klik hasil untuk salin)");
        histHeader.setPadding(10, 30, 10, 10);
        histHeader.setTypeface(null, Typeface.BOLD);
        
        RelativeLayout.LayoutParams headerParams = new RelativeLayout.LayoutParams(-1, -2);
        headerParams.addRule(RelativeLayout.BELOW, buttonArea.getId());
        histHeader.setLayoutParams(headerParams);
        root.addView(histHeader);

        ScrollView scrollHist = new ScrollView(this);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(-1, -1);
        scrollParams.addRule(RelativeLayout.BELOW, histHeader.getId());
        scrollHist.setLayoutParams(scrollParams);
        
        historyLayout = new LinearLayout(this);
        historyLayout.setOrientation(LinearLayout.VERTICAL);
        scrollHist.addView(historyLayout);
        root.addView(scrollHist);

        setContentView(root);
    }

    private void onButtonClick(String text) {
        if (text.equals("C")) {
            currentInput = "";
            tvDisplayExp.setText(" ");
            tvDisplayResult.setText("0");
            return;
        } else if (text.equals("=")) {
            String res = tvDisplayResult.getText().toString();
            if (!res.equals("0") && !res.equals("Error") && !currentInput.isEmpty()) {
                addToHistory(currentInput + " = " + res);
                currentInput = res; // Hasil jadi input baru
                tvDisplayExp.setText(res);
            }
        } else if (text.equals("sin") || text.equals("cos") || text.equals("tan") || text.equals("sqrt")) {
            currentInput += text + "(";
        } else if (text.equals("×")) {
             currentInput += "*";
        } else if (text.equals("÷")) {
             currentInput += "/";
        } else {
            currentInput += text;
        }
        
        // Update display ekspresi (selalu di bawah)
        String displayText = currentInput
            .replace("*", "×")
            .replace("/", "÷");
        tvDisplayExp.setText(displayText.isEmpty() ? " " : displayText);
        
        // Update hasil real-time (di atas)
        updateCalculation();
    }

    private void updateCalculation() {
        if (currentInput.isEmpty()) {
             tvDisplayResult.setText("0");
             return;
        }
        
        try {
            // Panggil Rust native function
            String res = calculateNative(currentInput, isDegree);
            tvDisplayResult.setText(res);
        } catch (Exception e) {
            tvDisplayResult.setText("Error");
        }
    }

    private void addToHistory(String item) {
        TextView tv = new TextView(this);
        tv.setText(item);
        tv.setPadding(15, 10, 15, 10);
        tv.setTextSize(16);
        tv.setBackgroundColor(Color.WHITE);
        
        // Tambahkan margin antar item riwayat
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 2, 0, 2);
        tv.setLayoutParams(params);

        tv.setOnClickListener(v -> copyToClipboard(item.split("=")[1].trim()));
        historyLayout.addView(tv, 0); // Tambah ke paling atas
    }

    private void copyToClipboard(String text) {
        if (text.isEmpty() || text.equals("0") || text.equals("Error")) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("calc_res", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied: " + text, Toast.LENGTH_SHORT).show();
    }
}
