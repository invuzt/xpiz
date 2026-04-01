package com.invuzt.xpiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    // Warna Odfiz Brik (Tua & Lavender)
    private final String CL_BG = "#FFFFFF"; // Latar belakang putih gading
    private final String CL_CARD = "#071D18"; // Hijau tua banget (hampir hitam)
    private final String CL_ACCENT = "#D0C9FF"; // Lavender pastel
    private final String CL_TEXT_ON_CARD = "#FFFFFF";
    private final String CL_TEXT_ON_BG = "#000000";

    private TextView totalView, aiNotif, txtInput;
    private FrameLayout progressBar;
    private int totalBelanja = 0, currentProgress = 79; // Contoh progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // --- ROOT CONTAINER ---
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor(CL_BG));
        root.setPadding(40, 60, 40, 0);

        // --- 1. HEADER (BRIK LOGO & LEVEL) ---
        RelativeLayout header = new RelativeLayout(this);
        root.addView(header, new LinearLayout.LayoutParams(-1, -2));

        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(32);
        logo.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        logo.setTextColor(Color.parseColor(CL_TEXT_ON_BG));
        header.addView(logo);

        TextView level = new TextView(this);
        level.setText("71 LEVEL");
        level.setTextColor(Color.parseColor(CL_TEXT_ON_BG));
        level.setPadding(30, 15, 30, 15);
        level.setTextSize(14);
        level.setBackground(createCurvedDrawable(Color.parseColor(CL_ACCENT), 50));
        
        RelativeLayout.LayoutParams lpLevel = new RelativeLayout.LayoutParams(-2, -2);
        lpLevel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(level, lpLevel);

        // --- 2. WELCOME CARD (LENGKUNG EKSTREM) ---
        addSpacer(root, 50);
        TextView welcome = new TextView(this);
        welcome.setText("Odfiz,\nselamat datang kembali");
        welcome.setTextSize(26);
        welcome.setLineSpacing(0, 1.2f);
        welcome.setTextColor(Color.parseColor(CL_TEXT_ON_CARD));
        welcome.setPadding(60, 60, 60, 60);
        // Kunci UI Lengkung Brik ada di radius 80 (sangat bulat)
        welcome.setBackground(createCurvedDrawable(Color.parseColor(CL_CARD), 80));
        root.addView(welcome);

        // --- 3. PROGRESS CARD (DIBUAT SEOLAH MENYATU) ---
        addSpacer(root, 10);
        LinearLayout cardProgress = new LinearLayout(this);
        cardProgress.setOrientation(LinearLayout.VERTICAL);
        cardProgress.setPadding(60, 60, 60, 60);
        // Radius 80 agar sejajar dengan welcome card
        cardProgress.setBackground(createCurvedDrawable(Color.parseColor(CL_CARD), 80));
        
        TextView progTitle = new TextView(this);
        progTitle.setText("Progress Anda");
        progTitle.setTextColor(Color.parseColor(CL_ACCENT));
        progTitle.setTextSize(16);
        cardProgress.addView(progTitle);

        TextView progSub = new TextView(this);
        progSub.setText("Jangan bolos jualan ya!");
        progSub.setTextColor(Color.GRAY);
        progSub.setTextSize(14);
        progSub.setPadding(0, 10, 0, 30);
        cardProgress.addView(progSub);

        // -- PROGRESS BAR CONTAINER --
        RelativeLayout pbContainer = new RelativeLayout(this);
        cardProgress.addView(pbContainer);

        TextView progText = new TextView(this);
        progText.setText(currentProgress + "%");
        progText.setTextColor(Color.parseColor(CL_TEXT_ON_CARD));
        progText.setTextSize(50);
        pbContainer.addView(progText);

        // Bikin Progress Bar Kotak-Kotak (Custom View)
        View pbGrid = createProgressBarGrid();
        RelativeLayout.LayoutParams lpProg = new RelativeLayout.LayoutParams(-2, 60);
        lpProg.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpProg.addRule(RelativeLayout.CENTER_VERTICAL);
        pbContainer.addView(pbGrid, lpProg);

        root.addView(cardProgress);

        // --- 4. INPUT ROW (SUBSTITUSI NEW CHALLENGE) ---
        addSpacer(root, 50);
        RelativeLayout inputRow = new RelativeLayout(this);
        root.addView(inputRow);

        TextView inputLabel = new TextView(this);
        inputLabel.setText("Input AI");
        inputLabel.setTextColor(Color.GRAY);
        inputLabel.setTextSize(16);
        inputRow.addView(inputLabel);

        // Ganti 'START' jadi tombol AI
        TextView btnAi = new TextView(this);
        btnAi.setText("PROSES");
        btnAi.setTextColor(Color.parseColor(CL_TEXT_ON_CARD));
        btnAi.setPadding(60, 25, 60, 25);
        btnAi.setBackground(createCurvedDrawable(Color.BLACK, 30));
        btnAi.setOnClickListener(v -> processInput());
        
        RelativeLayout.LayoutParams lpBtn = new RelativeLayout.LayoutParams(-2, -2);
        lpBtn.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        inputRow.addView(btnAi, lpBtn);

        // Teks Input yang diketik (Substitusi Rhythm Lightning)
        txtInput = new TextView(this);
        txtInput.setText("Menunggu perintah...");
        txtInput.setTextSize(26);
        txtInput.setTextColor(Color.parseColor(CL_TEXT_ON_BG));
        txtInput.setPadding(0, 20, 0, 0);
        
        RelativeLayout.LayoutParams lpInput = new RelativeLayout.LayoutParams(-1, -2);
        lpInput.addRule(RelativeLayout.BELOW, inputLabel.getId());
        root.addView(txtInput, lpInput);

        // --- 5. STATS ROW (OMSET & NOTIF) - Substitusi Best Score & Reaction ---
        addSpacer(root, 40);
        LinearLayout statsRow = new LinearLayout(this);
        root.addView(statsRow);

        // Kotak Omset (Kiri)
        LinearLayout cardOmset = createStatCard("Omset Hari Ini", "Rp 7.593", CL_ACCENT);
        statsRow.addView(cardOmset, new LinearLayout.LayoutParams(0, -2, 1.0f));

        addSpacerHorizontal(statsRow, 20);

        // Kotak Notif AI (Kanan)
        aiNotif = new TextView(this); // Pakai TextView langsung biar gampang update
        aiNotif.setText("Standby\n285 ms");
        aiNotif.setTextSize(24);
        aiNotif.setPadding(40, 40, 40, 40);
        aiNotif.setTextColor(Color.parseColor(CL_TEXT_ON_CARD));
        aiNotif.setBackground(createCurvedDrawable(Color.parseColor(CL_CARD), 80));
        statsRow.addView(aiNotif, new LinearLayout.LayoutParams(0, -1, 1.0f));

        // --- 6. FLOATING BAR (KASIR / LAPORAN) ---
        FrameLayout bottomNavContainer = new FrameLayout(this);
        bottomNavContainer.setPadding(30, 30, 30, 30);
        bottomNavContainer.setBackground(createCurvedDrawable(Color.BLACK, 100));
        
        LinearLayout bottomNav = new LinearLayout(this);
        bottomNav.setGravity(Gravity.CENTER);
        
        // Tombol PROGRESS (Lavender)
        TextView navProg = new TextView(this);
        navProg.setText("PROGRESS");
        navProg.setTextColor(Color.parseColor(CL_TEXT_ON_BG));
        navProg.setPadding(60, 30, 60, 30);
        navProg.setBackground(createCurvedDrawable(Color.parseColor(CL_ACCENT), 50));
        bottomNav.addView(navProg);
        
        // Tombol TRAINING (Polos)
        TextView navTrain = new TextView(this);
        navTrain.setText("LAPORAN");
        navTrain.setTextColor(Color.WHITE);
        navTrain.setPadding(60, 30, 60, 30);
        bottomNav.addView(navTrain);

        bottomNavContainer.addView(bottomNav);

        // Menaruh Nav Bar di paling bawah
        RelativeLayout finalLayout = new RelativeLayout(this);
        finalLayout.addView(root);
        
        RelativeLayout.LayoutParams lpNav = new RelativeLayout.LayoutParams(-1, -2);
        lpNav.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpNav.setMargins(60, 0, 60, 60);
        finalLayout.addView(bottomNavContainer, lpNav);

        // --- 7. INPUT HIDDEN (BUAT NGETIK) ---
        addHiddenInput(finalLayout);

        setContentView(finalLayout);
    }

    // --- UTILITY METHODS (BUAT LENGKUNGAN DLL) ---
    private GradientDrawable createCurvedDrawable(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

    private LinearLayout createStatCard(String title, String val, String valColor) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(40, 40, 40, 40);
        card.setBackground(createCurvedDrawable(Color.parseColor(CL_CARD), 80));

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.GRAY);
        tvTitle.setTextSize(14);
        card.addView(tvTitle);

        TextView tvVal = new TextView(this);
        tvVal.setText(val);
        tvVal.setTextColor(Color.parseColor(valColor));
        tvVal.setTextSize(32);
        tvVal.setPadding(0, 15, 0, 0);
        card.addView(tvVal);
        return card;
    }

    private View createProgressBarGrid() {
        // Ini bikin efek kotak-kotak lavender di progress bar
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.HORIZONTAL);
        int kotakLunas = (currentProgress / 5); // misal tiap kotak 5%
        for(int i=0; i<20; i++) {
            View v = new View(this);
            int color = (i < kotakLunas) ? Color.parseColor(CL_ACCENT) : Color.parseColor("#1A3A33");
            v.setBackground(createCurvedDrawable(color, 5));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15, -1);
            lp.setMargins(4, 0, 4, 0);
            grid.addView(v, lp);
        }
        return grid;
    }

    private void addSpacer(LinearLayout v, int h) {
        View s = new View(this);
        v.addView(s, new LinearLayout.LayoutParams(-1, h));
    }
    
    private void addSpacerHorizontal(LinearLayout v, int w) {
        View s = new View(this);
        v.addView(s, new LinearLayout.LayoutParams(w, -1));
    }

    // --- LOGIKA KASIR LAMA ---
    private void addHiddenInput(ViewGroup vg) {
        EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setImeOptions(EditorInfo.IME_ACTION_SEND);
        // Ngumpetin EditText biar nggak ngerusak UI Brik
        vg.addView(et, new ViewGroup.LayoutParams(1, 1)); 
        et.setOnEditorActionListener((v, id, ev) -> {
            txtInput.setText(et.getText().toString());
            et.setText(""); // Reset
            return true;
        });
        // Kalau layar di-tap, keyboard muncul
        vg.setOnClickListener(v -> { et.requestFocus(); });
    }
    
    private void processInput() {
        // Panggil native Rust di sini
        String cmd = txtInput.getText().toString();
        if(!cmd.equals("Menunggu perintah...")) {
            String res = predictBestButton(cmd);
            aiNotif.setText("Processed\n40 ms");
            // Logika ADD/PAY ditaruh di sini
        }
    }
}
