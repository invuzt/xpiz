package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    // Memanggil library Rust
    static { System.loadLibrary("hello"); }
    // Fungsi native simpel
    private native String getHelloFromRust();

    // Definisikan Warna Brik Style
    private final int CL_BG = Color.parseColor("#FFFFFF"); // Putih Gading
    private final int CL_CARD = Color.parseColor("#071D18"); // Hijau Tua Gelap
    private final int CL_ACCENT = Color.parseColor("#D0C9FF"); // Lavender Pastel

    private TextView txtRustMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // --- 0. ROOT WRAPPER (CONTAINER UTAMA) ---
        RelativeLayout rootWrap = new RelativeLayout(this);
        rootWrap.setBackgroundColor(CL_BG);

        // --- SCROLLVIEW AGAR BISA DI-SCROLL ---
        ScrollView scrollView = new ScrollView(this);
        rootWrap.addView(scrollView);

        // --- LINERLAYOUT UNTUK KONTEN VERTIKAL ---
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(50, 80, 50, 250); // Padding bawah ekstra buat nav bar
        scrollView.addView(content);

        // ==========================================
        // --- 1. HEADER (LOGO & LEVEL) ---
        // ==========================================
        RelativeLayout header = new RelativeLayout(this);
        content.addView(header);

        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(30);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        logo.setTextColor(Color.BLACK);
        header.addView(logo);

        TextView level = new TextView(this);
        level.setText("71 LEVEL");
        level.setTextColor(Color.BLACK);
        level.setPadding(30, 15, 30, 15);
        level.setTextSize(12);
        level.setBackground(createRounded(CL_ACCENT, 50));
        RelativeLayout.LayoutParams lpLevel = new RelativeLayout.LayoutParams(-2, -2);
        lpLevel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(level, lpLevel);

        // ==========================================
        // --- 2. WELCOME CARD ---
        // ==========================================
        addSpacer(content, 60);
        TextView welcome = new TextView(this);
        welcome.setText("Bohdan,\nwelcome back");
        welcome.setTextSize(24);
        welcome.setLineSpacing(0, 1.2f);
        welcome.setTextColor(Color.WHITE);
        welcome.setPadding(60, 70, 60, 70);
        welcome.setBackground(createRounded(CL_CARD, 90)); // Radius ekstrem
        content.addView(welcome);

        // ==========================================
        // --- 3. PROGRESS CARD ---
        // ==========================================
        addSpacer(content, 15);
        LinearLayout progressCard = new LinearLayout(this);
        progressCard.setOrientation(LinearLayout.VERTICAL);
        progressCard.setPadding(60, 60, 60, 60);
        progressCard.setBackground(createRounded(CL_CARD, 90));
        
        TextView progTitle = new TextView(this);
        progTitle.setText("Your progress");
        progTitle.setTextColor(CL_ACCENT);
        progressCard.addView(progTitle);

        TextView progSub = new TextView(this);
        progSub.setText("Try not to skip your training days.");
        progSub.setTextColor(Color.GRAY);
        progSub.setTextSize(12);
        progSub.setPadding(0, 5, 0, 30);
        progressCard.addView(progSub);

        // Progress Bar & Teks 79%
        RelativeLayout pbContainer = new RelativeLayout(this);
        progressCard.addView(pbContainer);

        TextView prog79 = new TextView(this);
        prog79.setText("79%");
        prog79.setTextColor(Color.WHITE);
        prog79.setTextSize(40);
        pbContainer.addView(prog79);

        // Dummy Progress Bar Kotak-Kotak lavender
        LinearLayout pbGrid = createDummyProgressBar();
        RelativeLayout.LayoutParams lpPb = new RelativeLayout.LayoutParams(-2, 50);
        lpPb.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpPb.addRule(RelativeLayout.CENTER_VERTICAL);
        pbContainer.addView(pbGrid, lpPb);

        content.addView(progressCard);

        // ==========================================
        // --- 4. NEW CHALLENGE (DUMMY) ---
        // ==========================================
        addSpacer(content, 50);
        TextView challengeLabel = new TextView(this);
        challengeLabel.setText("New challenge");
        challengeLabel.setTextColor(Color.GRAY);
        content.addView(challengeLabel);

        TextView challengeTitle = new TextView(this);
        challengeTitle.setText("Rhythm lightning");
        challengeTitle.setTextSize(24);
        challengeTitle.setTextColor(Color.BLACK);
        challengeTitle.setPadding(0, 10, 0, 0);
        content.addView(challengeTitle);

        // ==========================================
        // --- 5. STATS ROW (RUST MESSAGE) ---
        // ==========================================
        addSpacer(content, 40);
        LinearLayout statsRow = new LinearLayout(this);
        content.addView(statsRow);

        // Kotak Kiri (DUMMY BEST SCORE)
        LinearLayout cardLeft = createStatCard("Best score", "7,593");
        statsRow.addView(cardLeft, new LinearLayout.LayoutParams(0, -2, 1.0f));

        addSpacerHorizontal(statsRow, 20);

        // Kotak Kanan (DUMMY REACTION SPEED)
        LinearLayout cardRight = createStatCard("Reaction speed", "285 ms");
        statsRow.addView(cardRight, new LinearLayout.LayoutParams(0, -1, 1.0f));

        // ==========================================
        // --- 6. ACHIEVEMENTS (TEMPAT PESAN RUST) ---
        // ==========================================
        addSpacer(content, 50);
        TextView achieveLabel = new TextView(this);
        achieveLabel.setText("Achievements unlocked");
        achieveLabel.setTextColor(Color.GRAY);
        content.addView(achieveLabel);

        addSpacer(content, 20);
        LinearLayout rustCard = new LinearLayout(this);
        rustCard.setPadding(60, 60, 60, 60);
        rustCard.setBackground(createRounded(CL_CARD, 90));
        
        // Tempat Pesan dari Rust
        txtRustMessage = new TextView(this);
        txtRustMessage.setText(getHelloFromRust()); // Panggil Rust di sini
        txtRustMessage.setTextSize(18);
        txtRustMessage.setTextColor(Color.WHITE);
        rustCard.addView(txtRustMessage);
        
        content.addView(rustCard);

        // ==========================================
        // --- 7. FLOATING BOTTOM NAV BAR (DUMMY) ---
        // ==========================================
        FrameLayout navWrap = new FrameLayout(this);
        navWrap.setBackground(createRounded(Color.BLACK, 100));
        navWrap.setPadding(20, 20, 20, 20);
        
        LinearLayout navContent = new LinearLayout(this);
        navContent.setGravity(Gravity.CENTER);
        
        TextView btnProg = new TextView(this);
        btnProg.setText("PROGRESS");
        btnProg.setTextColor(Color.BLACK);
        btnProg.setPadding(50, 25, 50, 25);
        btnProg.setBackground(createRounded(CL_ACCENT, 50));
        navContent.addView(btnProg);
        
        TextView btnTrain = new TextView(this);
        btnTrain.setText("TRAINING");
        btnTrain.setTextColor(Color.WHITE);
        btnTrain.setPadding(50, 25, 50, 25);
        navContent.addView(btnTrain);
        
        navWrap.addView(navContent);

        // Tempel Nav Bar di bawah layar
        RelativeLayout.LayoutParams lpNav = new RelativeLayout.LayoutParams(-1, -2);
        lpNav.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpNav.setMargins(60, 0, 60, 60);
        rootWrap.addView(navWrap, lpNav);

        setContentView(rootWrap);
    }

    // ==========================================
    // --- UTILITY METHODS (GAK USAH DIOTAK-ATIK) ---
    // ==========================================
    private GradientDrawable createRounded(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

    private LinearLayout createStatCard(String title, String val) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(40, 40, 40, 40);
        card.setBackground(createRounded(CL_CARD, 80));
        TextView tvT = new TextView(this);
        tvT.setText(title); tvT.setTextColor(Color.GRAY);
        card.addView(tvT);
        TextView tvV = new TextView(this);
        tvV.setText(val); tvV.setTextSize(25); tvV.setTextColor(Color.WHITE);
        card.addView(tvV);
        return card;
    }

    private LinearLayout createDummyProgressBar() {
        LinearLayout l = new LinearLayout(this);
        for(int i=0; i<15; i++) {
            View v = new View(this);
            v.setBackground(createRounded((i < 11 ? CL_ACCENT : Color.parseColor("#1A3A33")), 5));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15, -1);
            lp.setMargins(4,0,4,0);
            l.addView(v, lp);
        }
        return l;
    }

    private void addSpacer(LinearLayout l, int h) {
        View s = new View(this);
        l.addView(s, new LinearLayout.LayoutParams(-1, h));
    }
    
    private void addSpacerHorizontal(LinearLayout l, int w) {
        View s = new View(this);
        l.addView(s, new LinearLayout.LayoutParams(w, -1));
    }
}
