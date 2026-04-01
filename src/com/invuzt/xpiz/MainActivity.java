package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import static com.invuzt.xpiz.BrikStyle.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }

    private native String getSystemConfig(String key);
    private native String getStyleConfig(int pageId);
    private native String getContentFromRust(int id);
    private native String handleTouch(String text);

    private LinearLayout contentArea, navContainer;
    private TextView tvLevel, tvLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                  WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor(getSystemConfig("COLOR_GELAP")));

        // Header
        RelativeLayout header = new RelativeLayout(this);
        header.setId(View.generateViewId());
        header.setPadding(60, 150, 60, 40);

        tvLogo = new TextView(this);
        tvLogo.setText(getSystemConfig("LOGO"));
        tvLogo.setTextSize(28);
        tvLogo.setTypeface(null, Typeface.BOLD);
        tvLogo.setTextColor(Color.parseColor(getSystemConfig("COLOR_PUTIH")));
        header.addView(tvLogo);

        tvLevel = new TextView(this);
        tvLevel.setText(getSystemConfig("NOTIF"));
        tvLevel.setPadding(30, 10, 30, 10);
        tvLevel.setBackground(bulat(Color.parseColor(getSystemConfig("COLOR_AKSEN")), 50));
        tvLevel.setTextColor(Color.BLACK);
        tvLevel.setTypeface(null, Typeface.BOLD);

        RelativeLayout.LayoutParams lpLvl = new RelativeLayout.LayoutParams(-2, -2);
        lpLvl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(tvLevel, lpLvl);
        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(40, 20, 40, 300);
        scroll.addView(contentArea);

        RelativeLayout.LayoutParams lpScroll = new RelativeLayout.LayoutParams(-1, -1);
        lpScroll.addRule(RelativeLayout.BELOW, header.getId());
        root.addView(scroll, lpScroll);

        navContainer = buatNavbar();
        RelativeLayout.LayoutParams lpNav = new RelativeLayout.LayoutParams(-2, -2);
        lpNav.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpNav.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lpNav.setMargins(0, 0, 0, 80);
        root.addView(navContainer, lpNav);

        setContentView(root);
        buka(1);
    }

    private LinearLayout buatNavbar() {
        LinearLayout n = new LinearLayout(this);
        n.setBackground(bulat(Color.BLACK, 150));
        n.setPadding(20, 20, 20, 20);
        return n;
    }

    void refreshNavbar() {
        navContainer.removeAllViews();
        String[] menus = getSystemConfig("NAVBAR").split("\\|");
        for (int i = 0; i < menus.length; i++) {
            final int id = i + 1;
            String[] style = getStyleConfig(id).split("\\|");
            
            TextView btn = new TextView(this);
            btn.setText(" " + menus[i] + " ");
            btn.setPadding(50, 25, 50, 25);
            btn.setTypeface(null, Typeface.BOLD);
            btn.setBackground(bulat(Color.parseColor(style[0]), 100));
            btn.setTextColor(Color.parseColor(style[1]));
            btn.setOnClickListener(v -> buka(id));
            navContainer.addView(btn);
        }
    }

    void buka(int id) {
        contentArea.removeAllViews();
        // Logika update konten juga mengupdate state halaman di Rust
        String dataDariRust = getContentFromRust(id); 
        
        refreshNavbar(); // Navbar berubah warna berdasarkan state Rust
        tvLevel.setText(getSystemConfig("NOTIF"));

        String[] lines = dataDariRust.split("\n");
        for (final String line : lines) {
            if (line.trim().isEmpty()) continue;
            TextView card = new TextView(this);
            card.setText(line);
            card.setBackground(card(id == 1 ? Color.WHITE : Color.parseColor("#1A1A1A"), 0, 0));
            card.setPadding(60, 60, 60, 60);
            card.setTextColor(id == 1 ? Color.BLACK : Color.WHITE);
            card.setTextSize(17);
            card.setTypeface(null, Typeface.BOLD);
            card.setOnClickListener(v -> {
                handleTouch(line); // Reaksi di Rust
                tvLevel.setText(getSystemConfig("NOTIF")); // Update label Level
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 30);
            contentArea.addView(card, lp);
        }
    }
}
