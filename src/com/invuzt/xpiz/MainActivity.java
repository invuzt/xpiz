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
    private native String getContentFromRust(int id);
    private native String handleTouch(String text);

    private LinearLayout contentArea;
    private TextView bProg, bTrain, tvLevel, tvLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                  WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(GELAP);

        // Header Area
        RelativeLayout header = new RelativeLayout(this);
        header.setId(View.generateViewId());
        header.setPadding(60, 150, 60, 40);

        tvLogo = new TextView(this);
        tvLogo.setText(getSystemConfig("LOGO")); // Dari Rust
        tvLogo.setTextSize(28);
        tvLogo.setTypeface(null, Typeface.BOLD);
        tvLogo.setTextColor(PUTIH);
        header.addView(tvLogo);

        tvLevel = new TextView(this);
        tvLevel.setText(getSystemConfig("NOTIF")); // Dari Rust
        tvLevel.setPadding(30, 10, 30, 10);
        tvLevel.setBackground(bulat(AKSEN, 50));
        tvLevel.setTextColor(Color.BLACK);
        tvLevel.setTypeface(null, Typeface.BOLD);

        RelativeLayout.LayoutParams lpLvl = new RelativeLayout.LayoutParams(-2, -2);
        lpLvl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(tvLevel, lpLvl);
        root.addView(header);

        // Content Area
        ScrollView scroll = new ScrollView(this);
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(40, 20, 40, 300);
        scroll.addView(contentArea);

        RelativeLayout.LayoutParams lpScroll = new RelativeLayout.LayoutParams(-1, -1);
        lpScroll.addRule(RelativeLayout.BELOW, header.getId());
        root.addView(scroll, lpScroll);

        // Navbar Dinamis dari Rust
        LinearLayout nav = buatNavbar();
        RelativeLayout.LayoutParams lpNav = new RelativeLayout.LayoutParams(-2, -2);
        lpNav.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpNav.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lpNav.setMargins(0, 0, 0, 80);
        root.addView(nav, lpNav);

        setContentView(root);
        buka(1);
    }

    private LinearLayout buatNavbar() {
        LinearLayout n = new LinearLayout(this);
        n.setBackground(bulat(Color.BLACK, 150));
        n.setPadding(20, 20, 20, 20);

        String rawNav = getSystemConfig("NAVBAR");
        String[] menus = rawNav.split("\\|");

        for (int i = 0; i < menus.length; i++) {
            TextView btn = new TextView(this);
            btn.setText(" " + menus[i] + " ");
            btn.setPadding(40, 20, 40, 20);
            btn.setTypeface(null, Typeface.BOLD);
            btn.setTextColor(Color.GRAY);
            final int pageId = i + 1;
            btn.setOnClickListener(v -> buka(pageId));
            n.addView(btn);
        }
        return n;
    }

    void buka(int id) {
        contentArea.removeAllViews();
        tvLevel.setText(getSystemConfig("NOTIF")); // Update Notif tiap pindah hal

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

            card.setOnClickListener(v -> {
                String reaksi = handleTouch(line);
                tvLevel.setText(getSystemConfig("NOTIF")); // Sync Notif Header dari Rust
                Toast.makeText(this, reaksi, Toast.LENGTH_SHORT).show();
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 30);
            contentArea.addView(card, lp);
        }
    }
}
