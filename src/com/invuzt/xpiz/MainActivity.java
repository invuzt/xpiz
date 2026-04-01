package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getHelloFromRust();

    private final int CL_BG = Color.parseColor("#FFFFFF"); 
    private final int CL_CARD = Color.parseColor("#071D18"); 
    private final int CL_ACCENT = Color.parseColor("#D0C9FF"); 

    private LinearLayout mainContentArea;
    private TextView btnProg, btnTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RelativeLayout rootWrap = new RelativeLayout(this);
        rootWrap.setBackgroundColor(CL_BG);

        ScrollView scrollView = new ScrollView(this);
        rootWrap.addView(scrollView);

        // Kontainer Konten yang bisa diganti-ganti (Swappable)
        mainContentArea = new LinearLayout(this);
        mainContentArea.setOrientation(LinearLayout.VERTICAL);
        mainContentArea.setPadding(50, 80, 50, 280); 
        scrollView.addView(mainContentArea);

        // --- BOTTOM NAV BAR ---
        FrameLayout navWrap = new FrameLayout(this);
        navWrap.setBackground(createRounded(Color.BLACK, 100));
        navWrap.setPadding(10, 10, 10, 10);
        
        LinearLayout navContent = new LinearLayout(this);
        navContent.setGravity(Gravity.CENTER);
        
        btnProg = new TextView(this);
        btnProg.setText("PROGRESS");
        btnProg.setPadding(50, 30, 50, 30);
        btnProg.setTypeface(Typeface.DEFAULT_BOLD);
        navContent.addView(btnProg);
        
        btnTrain = new TextView(this);
        btnTrain.setText("TRAINING");
        btnTrain.setPadding(50, 30, 50, 30);
        btnTrain.setTypeface(Typeface.DEFAULT_BOLD);
        navContent.addView(btnTrain);
        
        navWrap.addView(navContent);

        // LOGIKA PINDAH HALAMAN
        btnProg.setOnClickListener(v -> showProgressPage());
        btnTrain.setOnClickListener(v -> showTrainingPage());

        // Layout Nav Bar di bawah
        RelativeLayout.LayoutParams lpNav = new RelativeLayout.LayoutParams(-1, -2);
        lpNav.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpNav.setMargins(60, 0, 60, 60);
        rootWrap.addView(navWrap, lpNav);

        // Tampilan Awal
        showProgressPage();

        setContentView(rootWrap);
    }

    // --- HALAMAN 1: PROGRESS ---
    private void showProgressPage() {
        updateNavState(true);
        mainContentArea.removeAllViews();
        
        // Header
        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(30);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        logo.setTextColor(Color.BLACK);
        mainContentArea.addView(logo);

        addSpacer(mainContentArea, 60);
        
        // Card Welcome
        TextView card = new TextView(this);
        card.setText("Odfiz,\nProgress Hari Ini");
        card.setTextSize(24);
        card.setTextColor(Color.WHITE);
        card.setPadding(60, 80, 60, 80);
        card.setBackground(createRounded(CL_CARD, 90));
        mainContentArea.addView(card);
        
        addSpacer(mainContentArea, 20);
        
        // Info Status
        TextView status = new TextView(this);
        status.setText("Engine: Online\nTarget: Ponorogo");
        status.setTextColor(Color.GRAY);
        mainContentArea.addView(status);
    }

    // --- HALAMAN 2: TRAINING (DARI RUST) ---
    private void showTrainingPage() {
        updateNavState(false);
        mainContentArea.removeAllViews();

        TextView title = new TextView(this);
        title.setText("Training Mode");
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.BLACK);
        mainContentArea.addView(title);

        addSpacer(mainContentArea, 60);

        // Card dari Rust
        TextView rustCard = new TextView(this);
        rustCard.setText(getHelloFromRust());
        rustCard.setTextSize(22);
        rustCard.setTextColor(Color.BLACK);
        rustCard.setPadding(60, 100, 60, 100);
        rustCard.setBackground(createRounded(CL_ACCENT, 90));
        rustCard.setGravity(Gravity.CENTER);
        mainContentArea.addView(rustCard);
        
        addSpacer(mainContentArea, 30);
        
        TextView hint = new TextView(this);
        hint.setText("Tekan PROGRESS untuk kembali.");
        hint.setTextColor(Color.GRAY);
        hint.setGravity(Gravity.CENTER);
        mainContentArea.addView(hint);
    }

    // Update warna tombol navigasi
    private void updateNavState(boolean isProgress) {
        if (isProgress) {
            btnProg.setBackground(createRounded(CL_ACCENT, 80));
            btnProg.setTextColor(Color.BLACK);
            btnTrain.setBackground(null);
            btnTrain.setTextColor(Color.WHITE);
        } else {
            btnTrain.setBackground(createRounded(CL_ACCENT, 80));
            btnTrain.setTextColor(Color.BLACK);
            btnProg.setBackground(null);
            btnProg.setTextColor(Color.WHITE);
        }
    }

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
