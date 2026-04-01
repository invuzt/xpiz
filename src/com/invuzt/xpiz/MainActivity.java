package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.util.Random;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getHelloFromRust();

    // --- BRIK CSS COLOR PALETTE ---
    private final int CL_BG_OUTER = Color.parseColor("#F5F5F5"); 
    private final int CL_BG_INNER = Color.parseColor("#000000"); // Hitam Pekat
    private final int CL_ACCENT   = Color.parseColor("#D0C9FF"); // Lavender
    private final int CL_CARD_W   = Color.parseColor("#FFFFFF"); // Kartu Putih

    private LinearLayout mainContentArea;
    private TextView btnProg, btnTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // --- 1. OUTER WRAPPER (Abu-abu Muda) ---
        RelativeLayout rootWrap = new RelativeLayout(this);
        rootWrap.setBackgroundColor(CL_BG_OUTER);
        rootWrap.setPadding(30, 30, 30, 30); // Margin luar kayak di gambar

        // --- 2. INNER CONTAINER (Hitam + Rounding Besar) ---
        LinearLayout innerContainer = new LinearLayout(this);
        innerContainer.setOrientation(LinearLayout.VERTICAL);
        innerContainer.setBackground(createRounded(CL_BG_INNER, 120));
        
        // Margin bawah buat Nav Bar
        RelativeLayout.LayoutParams lpInner = new RelativeLayout.LayoutParams(-1, -1);
        lpInner.setMargins(0, 0, 0, 30);
        rootWrap.addView(innerContainer, lpInner);

        // --- 3. SHARED BRIK HEADER (BRIK® & LEVEL) ---
        addSharedHeader(innerContainer);

        // --- 4. SCROLLABLE CONTENT AREA ---
        ScrollView scrollView = new ScrollView(this);
        innerContainer.addView(scrollView);

        mainContentArea = new LinearLayout(this);
        mainContentArea.setOrientation(LinearLayout.VERTICAL);
        mainContentArea.setPadding(60, 40, 60, 300); // Padding dalam konten
        scrollView.addView(mainContentArea);

        // --- 5. BOTTOM NAV BAR (Brik Style) ---
        addBottomNav(rootWrap);

        // Tampilan Awal: Training (Sesuai Permintaan Persis Gambar)
        showTrainingPage();

        setContentView(rootWrap);
    }

    // ==========================================================
    // --- BRIK DESIGN SYSTEMS (CSS Class Emulation) ---
    // ==========================================================

    // A. Shared Header: BRIK® di kiri, LEVEL di kanan
    private void addSharedHeader(LinearLayout container) {
        RelativeLayout header = new RelativeLayout(this);
        header.setPadding(70, 70, 70, 40);
        container.addView(header);

        // Logo BRIK
        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(26);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        logo.setTextColor(Color.WHITE);
        header.addView(logo);

        // Pil Level
        TextView levelPill = new TextView(this);
        levelPill.setText("71 LEVEL");
        levelPill.setTextSize(14);
        levelPill.setTextColor(Color.BLACK);
        levelPill.setTypeface(Typeface.DEFAULT_BOLD);
        levelPill.setPadding(30, 15, 30, 15);
        levelPill.setBackground(createRounded(CL_ACCENT, 40));

        RelativeLayout.LayoutParams lpLevel = new RelativeLayout.LayoutParams(-2, -2);
        lpLevel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(levelPill, lpLevel);
    }

    // B. Kotak Input/Dropdown Putih (Rhythm match)
    private void addInputCard(LinearLayout container, String text) {
        RelativeLayout card = new RelativeLayout(this);
        card.setPadding(60, 50, 60, 50);
        card.setBackground(createRounded(CL_CARD_W, 100)); // Rounding 100 konsisten

        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(Color.BLACK);
        t.setTextSize(18);
        card.addView(t);

        // Ikon Panah Kanan (Simple Simbol)
        TextView arrow = new TextView(this);
        arrow.setText(">");
        arrow.setTextColor(Color.GRAY);
        arrow.setTextSize(18);
        RelativeLayout.LayoutParams lpArrow = new RelativeLayout.LayoutParams(-2, -2);
        lpArrow.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        card.addView(arrow, lpArrow);

        container.addView(card);
    }

    // C. Kotak Sequence rush (Rounding Unik - Big Top, Small Bottom)
    private void addRushHeader(LinearLayout container) {
        RelativeLayout card = new RelativeLayout(this);
        card.setPadding(60, 50, 60, 50);
        
        // CSS-like: border-radius: 100px 100px 30px 30px;
        float[] radii = {100f, 100f, 100f, 100f, 30f, 30f, 30f, 30f};
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(CL_BG_INNER); // Hitam tapi lebih terang dikit kalau perlu
        gd.setCornerRadii(radii);
        card.setBackground(gd);

        TextView t = new TextView(this);
        t.setText("Sequence rush");
        t.setTextColor(Color.WHITE);
        t.setTextSize(18);
        card.addView(t);

        // Ikon Panah Bawah (Simple)
        TextView arrow = new TextView(this);
        arrow.setText("v");
        arrow.setTextColor(Color.GRAY);
        arrow.setTextSize(16);
        RelativeLayout.LayoutParams lpArrow = new RelativeLayout.LayoutParams(-2, -2);
        lpArrow.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        card.addView(arrow, lpArrow);

        container.addView(card);
    }

    // D. Kotak Skor & Reaksi (Dua Kolom)
    private void addScoreSection(LinearLayout container) {
        // Gabungkan dalam satu layout buat rounding induk
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        //CSS: radii: 30px 30px 100px 100px;
        float[] radii = {30f, 30f, 30f, 30f, 100f, 100f, 100f, 100f};
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(CL_BG_INNER);
        gd.setCornerRadii(radii);
        wrap.setBackground(gd);
        wrap.setPadding(60, 60, 60, 80);
        container.addView(wrap);

        LinearLayout row = new LinearLayout(this);
        row.setWeightSum(2f);
        wrap.addView(row);

        // Kolom 1 (Best Score)
        addScoreItem(row, "Best score", "Expert game mode", "2,435");
        
        // Kolom 2 (Reaction speed)
        addScoreItem(row, "Reaction speed", "Average time", "319 ms");
    }

    private void addScoreItem(LinearLayout row, String l1, String l2, String val) {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1f);
        col.setLayoutParams(lp);

        TextView tv1 = new TextView(this);
        tv1.setText(l1);
        tv1.setTextColor(Color.WHITE);
        tv1.setTextSize(15);
        col.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(l2);
        tv2.setTextColor(Color.GRAY);
        tv2.setTextSize(13);
        col.addView(tv2);

        addSpacer(col, 20);

        TextView tvVal = new TextView(this);
        tvVal.setText(val);
        tvVal.setTextColor(Color.WHITE);
        tvVal.setTextSize(36);
        tvVal.setTypeface(Typeface.DEFAULT_BOLD);
        col.addView(tvVal);

        row.addView(col);
    }

    // E. Komponen Grafik Progres (SIMULASI)
    private void addProgressCard(LinearLayout container) {
        addSpacer(container, 20);
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(60, 60, 60, 60);
        card.setBackground(createRounded(CL_BG_INNER, 100));
        container.addView(card);

        RelativeLayout headerRow = new RelativeLayout(this);
        card.addView(headerRow);

        // Label Kiri
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        headerRow.addView(labels);

        TextView t1 = new TextView(this);
        t1.setText("Your progress");
        t1.setTextColor(Color.WHITE);
        t1.setTextSize(17);
        labels.addView(t1);

        TextView t2 = new TextView(this);
        t2.setText("Track your metrics");
        t2.setTextColor(Color.GRAY);
        t2.setTextSize(13);
        labels.addView(t2);

        // Tombol Details Kanan
        TextView btnDetails = new TextView(this);
        btnDetails.setText("DETAILS");
        btnDetails.setTextColor(Color.WHITE);
        btnDetails.setTextSize(12);
        btnDetails.setTypeface(Typeface.DEFAULT_BOLD);
        btnDetails.setPadding(35, 15, 35, 15);
        btnDetails.setGravity(Gravity.CENTER);
        
        // CSS: Outline/Bordered
        GradientDrawable gdBtn = new GradientDrawable();
        gdBtn.setStroke(3, Color.GRAY);
        gdBtn.setCornerRadius(50);
        btnDetails.setBackground(gdBtn);

        RelativeLayout.LayoutParams lpDetails = new RelativeLayout.LayoutParams(-2, -2);
        lpDetails.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        headerRow.addView(btnDetails, lpDetails);

        addSpacer(card, 50);

        // --- BAR CHART SIMULATION ---
        LinearLayout chartRow = new LinearLayout(this);
        chartRow.setGravity(Gravity.BOTTOM);
        chartRow.setWeightSum(7f); // 7 Hari
        card.addView(chartRow);

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Random r = new Random();

        for (int i = 0; i < 7; i++) {
            LinearLayout dayCol = new LinearLayout(this);
            dayCol.setOrientation(LinearLayout.VERTICAL);
            dayCol.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lpCol = new LinearLayout.LayoutParams(0, -2, 1f);
            dayCol.setLayoutParams(lpCol);

            // Bar Lavender
            int barHeight = 80 + r.nextInt(150); // Tinggi acak
            if(i == 5) barHeight = 220; // Sat paling tinggi kayak di gambar

            View bar = new View(this);
            bar.setBackground(createRounded(CL_ACCENT, 20));
            LinearLayout.LayoutParams lpBar = new LinearLayout.LayoutParams(50, barHeight);
            bar.setLayoutParams(lpBar);
            dayCol.addView(bar);

            addSpacer(dayCol, 15);

            // Nama Hari
            TextView tvDay = new TextView(this);
            tvDay.setText(days[i]);
            tvDay.setTextColor(Color.GRAY);
            tvDay.setTextSize(11);
            dayCol.addView(tvDay);

            chartRow.addView(dayCol);
        }
    }

    // F. Komponen Dua Tombol Bawah (Start & View Leaders)
    private void addActionButtons(LinearLayout container) {
        addSpacer(container, 30);
        LinearLayout row = new LinearLayout(this);
        row.setWeightSum(2f);
        container.addView(row);

        // 1. START GAME (Penuh Lavender)
        TextView btnStart = new TextView(this);
        btnStart.setText("START GAME");
        btnStart.setTextColor(Color.BLACK);
        btnStart.setTypeface(Typeface.DEFAULT_BOLD);
        btnStart.setGravity(Gravity.CENTER);
        btnStart.setPadding(0, 50, 0, 50);
        btnStart.setBackground(createRounded(CL_ACCENT, 100));

        LinearLayout.LayoutParams lpStart = new LinearLayout.LayoutParams(0, -2, 1f);
        lpStart.setMargins(0, 0, 15, 0);
        btnStart.setLayoutParams(lpStart);
        row.addView(btnStart);

        // 2. VIEW LEADERS (Bergaris/Outline)
        TextView btnLeaders = new TextView(this);
        btnLeaders.setText("VIEW LEADERS");
        btnLeaders.setTextColor(Color.WHITE);
        btnLeaders.setTypeface(Typeface.DEFAULT_BOLD);
        btnLeaders.setGravity(Gravity.CENTER);
        btnLeaders.setPadding(0, 50, 0, 50);
        
        GradientDrawable gdLeaders = new GradientDrawable();
        gdLeaders.setStroke(3, Color.GRAY);
        gdLeaders.setCornerRadius(100);
        btnLeaders.setBackground(gdLeaders);

        LinearLayout.LayoutParams lpLead = new LinearLayout.LayoutParams(0, -2, 1f);
        lpLead.setMargins(15, 0, 0, 0);
        btnLeaders.setLayoutParams(lpLead);
        row.addView(btnLeaders);
    }

    // G. Bottom Nav Bar (Brik Style Persis Gambar)
    private void addBottomNav(RelativeLayout root) {
        // Kontainer Hitam Transparan/Blur kayak di gambar
        LinearLayout navWrap = new LinearLayout(this);
        navWrap.setBackground(createRounded(CL_BG_INNER, 100));
        navWrap.setPadding(10, 10, 10, 10);
        navWrap.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams lpNav = new RelativeLayout.LayoutParams(-1, -2);
        lpNav.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpNav.setMargins(60, 0, 60, 30); // Jarak dari bawah
        root.addView(navWrap, lpNav);

        // Ikon Bendera (Kiri)
        TextView iconFlag = createNavIcon("¶"); // Bendera pake simbol
        navWrap.addView(iconFlag);

        // Segmented Control (Tengah)
        LinearLayout segmented = new LinearLayout(this);
        segmented.setBackground(createRounded(Color.parseColor("#071D18"), 80)); // Hitam Gelap
        navWrap.addView(segmented);

        btnProg = createNavPill("PROGRESS", false);
        segmented.addView(btnProg);

        btnTrain = createNavPill("TRAINING", true); // TRAINING Aktif sesuai gambar
        segmented.addView(btnTrain);

        // Ikon Titik Tiga (Kanan)
        TextView iconMore = createNavIcon("•••");
        navWrap.addView(iconMore);

        // Logika Klik (Sederhana)
        btnProg.setOnClickListener(v -> showProgressPage());
        btnTrain.setOnClickListener(v -> showTrainingPage());
    }

    private TextView createNavIcon(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(Color.WHITE);
        t.setTextSize(18);
        t.setPadding(40, 30, 40, 30);
        t.setBackground(createRounded(CL_BG_INNER, 80));
        return t;
    }

    private TextView createNavPill(String text, boolean active) {
        TextView pill = new TextView(this);
        pill.setText(text);
        pill.setPadding(60, 30, 60, 30);
        pill.setTypeface(Typeface.DEFAULT_BOLD);
        if(active) {
            pill.setBackground(createRounded(CL_ACCENT, 80));
            pill.setTextColor(Color.BLACK);
        } else {
            pill.setTextColor(Color.WHITE);
        }
        return pill;
    }


    // ==========================================================
    // --- PAGE SWAPPER LOGIC ---
    // ==========================================================

    // --- HALAMAN 1: PROGRESS (Placeholder) ---
    private void showProgressPage() {
        mainContentArea.removeAllViews();
        addInputCard(mainContentArea, "Halaman Progress (Placeholder)");
    }

    // --- HALAMAN 2: TRAINING (PERSIS GAMBAR) ---
    private void showTrainingPage() {
        mainContentArea.removeAllViews();

        // 1. Kotak Input Putih (Dua baris konsisten)
        addInputCard(mainContentArea, "Rhythm match");
        addSpacer(mainContentArea, 10);
        addInputCard(mainContentArea, "Rhythm match");

        addSpacer(mainContentArea, 40);

        // 2. Sequence rush section (Header + Score + Chart)
        addRushHeader(mainContentArea);
        addScoreSection(mainContentArea); // Menyatu visual ke Rush Header
        
        addSpacer(mainContentArea, 40);

        // 3. Grafik Progress
        addProgressCard(mainContentArea);

        addSpacer(mainContentArea, 30);

        // 4. Tombol Aksi Bawah
        addActionButtons(mainContentArea);
    }

    // --- SHARED UTILS ---
    private GradientDrawable createRounded(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

    private void addSpacer(LinearLayout l, int h) {
        View s = new View(this);
        l.addView(s, new LinearLayout.LayoutParams(-1, h));
    }
}
