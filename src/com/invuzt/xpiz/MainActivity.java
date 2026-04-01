package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getContentFromRust(int pageId);
    
    // --- CSS STYLE ---
    static class Style {
        static final int BG = Color.parseColor("#081512");
        static final int AKSEN = Color.parseColor("#D0C9FF");
        static final int PUTIH = Color.WHITE;
        static GradientDrawable bulat(int warna, int radius) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(warna);
            gd.setCornerRadius(radius);
            return gd;
        }
    }

    private LinearLayout contentArea;
    private TextView btnProg, btnTrain, btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Root Container
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Style.BG);

        // 1. Header (Logo)
        TextView logo = new TextView(this);
        logo.setId(View.generateViewId());
        logo.setText("XPIZ®");
        logo.setTextSize(26);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setTextColor(Style.PUTIH);
        logo.setPadding(60, 120, 0, 40);
        root.addView(logo);

        // 2. Navigasi Bawah (Dibuat dulu supaya bisa jadi patokan)
        LinearLayout nav = buatNavigasi();
        nav.setId(View.generateViewId());
        RelativeLayout.LayoutParams navParams = new RelativeLayout.LayoutParams(-2, -2);
        navParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        navParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        navParams.setMargins(0, 0, 0, 100);
        root.addView(nav, navParams);

        // 3. Area Konten (Di tengah antara Logo dan Navigasi)
        ScrollView scroll = new ScrollView(this);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(-1, -1);
        scrollParams.addRule(RelativeLayout.BELOW, logo.getId());
        scrollParams.addRule(RelativeLayout.ABOVE, nav.getId());
        scrollParams.setMargins(60, 20, 60, 20);
        
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(contentArea);
        root.addView(scroll, scrollParams);

        setContentView(root);
        bukaTraining();
    }

    private LinearLayout buatNavigasi() {
        LinearLayout n = new LinearLayout(this);
        n.setBackground(Style.bulat(Color.BLACK, 100));
        n.setPadding(20, 15, 20, 15);
        
        btnProg = buatTombol(" PROGRESS ");
        btnTrain = buatTombol(" TRAINING ");
        btnAbout = buatTombol("  •••  ");

        btnProg.setOnClickListener(v -> bukaHalaman(2));
        btnTrain.setOnClickListener(v -> bukaHalaman(1));
        btnAbout.setOnClickListener(v -> bukaHalaman(3));

        n.addView(btnProg);
        n.addView(btnTrain);
        n.addView(btnAbout);
        return n;
    }

    private TextView buatTombol(String teks) {
        TextView tv = new TextView(this);
        tv.setText(teks);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(40, 30, 40, 30);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    void bukaHalaman(int id) {
        contentArea.removeAllViews();
        updateTombol(id);
        
        TextView t = new TextView(this);
        t.setTextColor(Style.PUTIH);
        t.setTextSize(19);
        t.setLineSpacing(10, 1.2f);
        
        if (id == 3) {
            t.setText("XPIZ SYSTEM\n\nStatus: Online\nEngine: Rust Core\nUI: Java Dynamic\n\nBuild 2026.04");
        } else {
            t.setText(getContentFromRust(id));
        }
        contentArea.addView(t);
    }

    void updateTombol(int activeId) {
        btnProg.setBackground(activeId == 2 ? Style.bulat(Style.AKSEN, 80) : null);
        btnProg.setTextColor(activeId == 2 ? Color.BLACK : Style.PUTIH);
        
        btnTrain.setBackground(activeId == 1 ? Style.bulat(Style.AKSEN, 80) : null);
        btnTrain.setTextColor(activeId == 1 ? Color.BLACK : Style.PUTIH);
        
        btnAbout.setBackground(activeId == 3 ? Style.bulat(Style.AKSEN, 80) : null);
        btnAbout.setTextColor(activeId == 3 ? Color.BLACK : Style.PUTIH);
    }
    
    void bukaTraining() { bukaHalaman(1); }
}
