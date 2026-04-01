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

    private TextView txtRustMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RelativeLayout rootWrap = new RelativeLayout(this);
        rootWrap.setBackgroundColor(CL_BG);

        ScrollView scrollView = new ScrollView(this);
        rootWrap.addView(scrollView);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(50, 80, 50, 280); 
        scrollView.addView(content);

        // --- HEADER ---
        RelativeLayout header = new RelativeLayout(this);
        content.addView(header);
        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(30);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        logo.setTextColor(Color.BLACK);
        header.addView(logo);

        // --- WELCOME CARD ---
        addSpacer(content, 60);
        TextView welcome = new TextView(this);
        welcome.setText("Odfiz,\nwelcome back");
        welcome.setTextSize(24);
        welcome.setTextColor(Color.WHITE);
        welcome.setPadding(60, 70, 60, 70);
        welcome.setBackground(createRounded(CL_CARD, 90));
        content.addView(welcome);

        // --- PROGRESS CARD ---
        addSpacer(content, 15);
        LinearLayout progressCard = new LinearLayout(this);
        progressCard.setOrientation(LinearLayout.VERTICAL);
        progressCard.setPadding(60, 60, 60, 60);
        progressCard.setBackground(createRounded(CL_CARD, 90));
        
        TextView prog79 = new TextView(this);
        prog79.setText("79%");
        prog79.setTextColor(Color.WHITE);
        prog79.setTextSize(40);
        progressCard.addView(prog79);
        content.addView(progressCard);

        // --- RUST MESSAGE CARD ---
        addSpacer(content, 50);
        TextView achieveLabel = new TextView(this);
        achieveLabel.setText("Rust Engine Status");
        achieveLabel.setTextColor(Color.GRAY);
        content.addView(achieveLabel);

        addSpacer(content, 20);
        LinearLayout rustCard = new LinearLayout(this);
        rustCard.setPadding(60, 60, 60, 60);
        rustCard.setBackground(createRounded(CL_CARD, 90));
        
        txtRustMessage = new TextView(this);
        txtRustMessage.setText(getHelloFromRust());
        txtRustMessage.setTextSize(18);
        txtRustMessage.setTextColor(Color.WHITE);
        rustCard.addView(txtRustMessage);
        content.addView(rustCard);

        // --- FLOATING NAV BAR (LOGIC ADDED) ---
        FrameLayout navWrap = new FrameLayout(this);
        navWrap.setBackground(createRounded(Color.BLACK, 100));
        navWrap.setPadding(10, 10, 10, 10);
        
        LinearLayout navContent = new LinearLayout(this);
        navContent.setGravity(Gravity.CENTER);
        
        // Tombol Progress
        TextView btnProg = new TextView(this);
        btnProg.setText("PROGRESS");
        btnProg.setPadding(50, 30, 50, 30);
        btnProg.setBackground(createRounded(CL_ACCENT, 80)); // Aktif pertama kali
        btnProg.setTextColor(Color.BLACK);
        btnProg.setClickable(true);
        navContent.addView(btnProg);
        
        // Tombol Training
        TextView btnTrain = new TextView(this);
        btnTrain.setText("TRAINING");
        btnTrain.setPadding(50, 30, 50, 30);
        btnTrain.setTextColor(Color.WHITE);
        btnTrain.setClickable(true);
        navContent.addView(btnTrain);
        
        navWrap.addView(navContent);

        // LOGIKA KLIK TOMBOL
        btnProg.setOnClickListener(v -> {
            btnProg.setBackground(createRounded(CL_ACCENT, 80));
            btnProg.setTextColor(Color.BLACK);
            btnTrain.setBackground(null);
            btnTrain.setTextColor(Color.WHITE);
            txtRustMessage.setText("Kembali ke Progress...");
            Toast.makeText(this, "Halaman Progress", Toast.LENGTH_SHORT).show();
        });

        btnTrain.setOnClickListener(v -> {
            btnTrain.setBackground(createRounded(CL_ACCENT, 80));
            btnTrain.setTextColor(Color.BLACK);
            btnProg.setBackground(null);
            btnProg.setTextColor(Color.WHITE);
            txtRustMessage.setText("Mode Training Aktif!");
            Toast.makeText(this, "Halaman Training", Toast.LENGTH_SHORT).show();
        });

        RelativeLayout.LayoutParams lpNav = new RelativeLayout.LayoutParams(-1, -2);
        lpNav.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpNav.setMargins(60, 0, 60, 60);
        rootWrap.addView(navWrap, lpNav);

        setContentView(rootWrap);
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
