package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import static com.invuzt.xpiz.BrikStyle.*;

public class MainActivity extends Activity {
    // Memuat library native Rust
    static { System.loadLibrary("hello"); }

    // Definisi fungsi native Rust
    private native String getContentFromRust(int id);
    private native String handleTouch(String text);

    private LinearLayout contentArea;
    private TextView bProg, bTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Membuat UI Fullscreen (No Action Bar & Status Bar overlay)
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                  WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(GELAP);

        // Header Area (Logo & Level)
        RelativeLayout header = new RelativeLayout(this);
        header.setId(View.generateViewId());
        header.setPadding(60, 150, 60, 40);

        TextView logo = new TextView(this);
        logo.setText("XPIZ®");
        logo.setTextSize(28);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setTextColor(PUTIH);
        header.addView(logo);

        TextView level = new TextView(this);
        level.setText("71 LEVEL");
        level.setPadding(30, 10, 30, 10);
        level.setBackground(bulat(AKSEN, 50));
        level.setTextColor(Color.BLACK);
        level.setTypeface(null, Typeface.BOLD);

        RelativeLayout.LayoutParams lpLvl = new RelativeLayout.LayoutParams(-2, -2);
        lpLvl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(level, lpLvl);
        root.addView(header);

        // Scrollable Content Area
        ScrollView scroll = new ScrollView(this);
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(40, 20, 40, 300);
        scroll.addView(contentArea);

        RelativeLayout.LayoutParams lpScroll = new RelativeLayout.LayoutParams(-1, -1);
        lpScroll.addRule(RelativeLayout.BELOW, header.getId());
        root.addView(scroll, lpScroll);

        // Navigation Bar (Floating at Bottom)
        LinearLayout nav = buatNavbar();
        RelativeLayout.LayoutParams lpNav = new RelativeLayout.LayoutParams(-2, -2);
        lpNav.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpNav.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lpNav.setMargins(0, 0, 0, 80);
        root.addView(nav, lpNav);

        setContentView(root);
        buka(1); // Default ke halaman Training
    }

    private LinearLayout buatNavbar() {
        LinearLayout n = new LinearLayout(this);
        n.setBackground(bulat(Color.BLACK, 150));
        n.setPadding(15, 15, 15, 15);

        bProg = new TextView(this); bProg.setText(" PROGRESS ");
        bTrain = new TextView(this); bTrain.setText(" TRAINING ");

        configBtn(bProg, 2); 
        configBtn(bTrain, 1);

        n.addView(bProg); 
        n.addView(bTrain);
        return n;
    }

    private void configBtn(TextView tv, int id) {
        tv.setPadding(50, 30, 50, 30);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setOnClickListener(v -> buka(id));
    }

    void buka(int id) {
        contentArea.removeAllViews();
        
        // Update visual state tombol navbar
        bProg.setBackground(id == 2 ? bulat(AKSEN, 100) : null);
        bProg.setTextColor(id == 2 ? Color.BLACK : Color.GRAY);
        bTrain.setBackground(id == 1 ? bulat(AKSEN, 100) : null);
        bTrain.setTextColor(id == 1 ? Color.BLACK : Color.GRAY);

        // Ambil data dinamis dari Rust
        String dataDariRust = getContentFromRust(id);
        String[] lines = dataDariRust.split("\n");

        for (final String line : lines) {
            if (line.trim().isEmpty()) continue;

            TextView card = new TextView(this);
            card.setText(line);
            card.setBackground(card(id == 1 ? PUTIH : ABU_TUA, Color.TRANSPARENT, 0));
            card.setPadding(60, 60, 60, 60);
            card.setTextColor(id == 1 ? Color.BLACK : PUTIH);
            card.setTextSize(17);
            card.setTypeface(null, Typeface.BOLD);

            // Reaksi saat kartu disentuh
            card.setOnClickListener(v -> {
                String reaksi = handleTouch(line);
                Toast.makeText(this, reaksi, Toast.LENGTH_SHORT).show();
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 30);
            contentArea.addView(card, lp);
        }
    }
}
