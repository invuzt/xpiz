package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import static com.invuzt.xpiz.BrikStyle.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getSystemConfig(String k);
    private native String getStyleConfig(int id);
    private native String getContentFromRust(int id);
    private native String handleTouch(String t);

    private LinearLayout contentArea, navContainer;
    private TextView tvLevel, tvLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen Mode
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, 
                  WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor(getSystemConfig("COLOR_GELAP")));

        // 1. Header (Logo & Notif)
        RelativeLayout header = new RelativeLayout(this);
        header.setId(View.generateViewId());
        header.setPadding(60, 150, 60, 40);

        tvLogo = new TextView(this);
        tvLogo.setText(getSystemConfig("LOGO"));
        tvLogo.setTextSize(28);
        tvLogo.setTypeface(null, Typeface.BOLD);
        tvLogo.setTextColor(Color.parseColor("#FFFFFF"));
        header.addView(tvLogo);

        tvLevel = new TextView(this);
        tvLevel.setText(getSystemConfig("NOTIF"));
        tvLevel.setPadding(35, 12, 35, 12);
        tvLevel.setBackground(bulat(Color.parseColor("#D0C9FF"), 50));
        tvLevel.setTextColor(Color.BLACK);
        tvLevel.setTypeface(null, Typeface.BOLD);

        RelativeLayout.LayoutParams lpLvl = new RelativeLayout.LayoutParams(-2, -2);
        lpLvl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(tvLevel, lpLvl);
        root.addView(header);

        // 2. Scrollable Content Area
        ScrollView scroll = new ScrollView(this);
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(40, 20, 40, 350); // Padding bawah agar tidak tertutup navbar
        scroll.addView(contentArea);

        RelativeLayout.LayoutParams lpScroll = new RelativeLayout.LayoutParams(-1, -1);
        lpScroll.addRule(RelativeLayout.BELOW, header.getId());
        root.addView(scroll, lpScroll);

        // 3. Floating Navbar Container
        navContainer = new LinearLayout(this);
        navContainer.setBackground(bulat(Color.BLACK, 150));
        navContainer.setPadding(20, 20, 20, 20);
        
        RelativeLayout.LayoutParams lpNav = new RelativeLayout.LayoutParams(-2, -2);
        lpNav.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpNav.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lpNav.setMargins(0, 0, 0, 80);
        root.addView(navContainer, lpNav);

        setContentView(root);
        buka(1);
    }

    void refreshNavbar() {
        navContainer.removeAllViews();
        String[] menus = getSystemConfig("NAVBAR").split("\\|");
        for (int i = 0; i < menus.length; i++) {
            final int pageId = i + 1;
            String[] stl = getStyleConfig(pageId).split("\\|");
            
            TextView btn = new TextView(this);
            btn.setText(" " + menus[i] + " ");
            btn.setPadding(45, 25, 45, 25);
            btn.setTypeface(null, Typeface.BOLD);
            btn.setBackground(bulat(Color.parseColor(stl[0]), 100));
            btn.setTextColor(Color.parseColor(stl[1]));
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.setMargins(10, 0, 10, 0);
            btn.setOnClickListener(v -> buka(pageId));
            navContainer.addView(btn, lp);
        }
    }

    void buka(int id) {
        contentArea.removeAllViews();
        String data = getContentFromRust(id);
        tvLevel.setText(getSystemConfig("NOTIF"));
        refreshNavbar();

        for (String line : data.split("\n")) {
            if (line.trim().isEmpty()) continue;
            
            TextView card = new TextView(this);
            card.setText(line);
            // Style card sederhana (Warna diambil dari state ID)
            int bg = (id == 1) ? Color.WHITE : Color.parseColor("#1A1A1A");
            int txt = (id == 1) ? Color.BLACK : Color.WHITE;
            
            card.setBackground(card(bg, 0, 0));
            card.setPadding(60, 60, 60, 60);
            card.setTextColor(txt);
            card.setTextSize(17);
            card.setTypeface(null, Typeface.BOLD);

            card.setOnClickListener(v -> {
                handleTouch(line);
                tvLevel.setText(getSystemConfig("NOTIF"));
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 30);
            contentArea.addView(card, lp);
        }
    }
}
