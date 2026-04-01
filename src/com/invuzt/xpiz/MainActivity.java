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
        getWindow().setFlags(512, 512);

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
        tvLogo.setTextColor(Color.WHITE);
        // KLIK LOGO UNTUK SETTINGS
        tvLogo.setOnClickListener(v -> {
            String cmd = handleTouch("HEADER_CLICK");
            if(cmd.startsWith("GOTO:")) buka(99);
        });
        header.addView(tvLogo);

        tvLevel = new TextView(this);
        tvLevel.setText(getSystemConfig("NOTIF"));
        tvLevel.setPadding(35, 12, 35, 12);
        tvLevel.setBackground(bulat(Color.parseColor("#D0C9FF"), 50));
        tvLevel.setTextColor(Color.BLACK);
        tvLevel.setOnClickListener(v -> { handleTouch("NOTIF_CLICK"); buka(1); });
        
        RelativeLayout.LayoutParams lpL = new RelativeLayout.LayoutParams(-2,-2);
        lpL.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(tvLevel, lpL);
        root.addView(header);

        // Content
        ScrollView scroll = new ScrollView(this);
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(1);
        contentArea.setPadding(40, 20, 40, 400);
        scroll.addView(contentArea);
        RelativeLayout.LayoutParams lpS = new RelativeLayout.LayoutParams(-1,-1);
        lpS.addRule(RelativeLayout.BELOW, header.getId());
        root.addView(scroll, lpS);

        // Navbar
        HorizontalScrollView ns = new HorizontalScrollView(this);
        ns.setBackground(bulat(Color.BLACK, 150));
        navContainer = new LinearLayout(this);
        navContainer.setPadding(20,20,20,20);
        ns.addView(navContainer);
        RelativeLayout.LayoutParams lpN = new RelativeLayout.LayoutParams(-2,-2);
        lpN.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpN.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lpN.setMargins(0,0,0,80);
        root.addView(ns, lpN);

        setContentView(root);
        buka(1);
    }

    void buka(int id) {
        contentArea.removeAllViews();
        String data = getContentFromRust(id);
        tvLevel.setText(getSystemConfig("NOTIF"));
        
        // Render Navbar (Hanya Training & Progress)
        navContainer.removeAllViews();
        String[] menus = getSystemConfig("NAVBAR").split("\\|");
        for(int i=0; i<menus.length; i++) {
            final int pid = i+1;
            TextView b = new TextView(this);
            b.setText(" "+menus[i]+" ");
            b.setPadding(40,20,40,20);
            b.setTextColor(Color.WHITE);
            b.setOnClickListener(v -> buka(pid));
            navContainer.addView(b);
        }

        // Render Modular Items (Teks + Input)
        for (String line : data.split("\n")) {
            String[] part = line.split("\\|");
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(1);
            item.setBackground(card(Color.parseColor("#1A1A1A"), 0, 0));
            item.setPadding(50,50,50,50);

            TextView label = new TextView(this);
            label.setText(part[0]);
            label.setTextColor(Color.WHITE);
            label.setTypeface(null, Typeface.BOLD);
            item.addView(label);

            if(part[1].equals("INPUT")) {
                EditText et = new EditText(this);
                et.setHint("Ketik di sini...");
                et.setHintTextColor(Color.GRAY);
                et.setTextColor(Color.CYAN);
                item.addView(et);
            }

            item.setOnClickListener(v -> {
                String res = handleTouch(part[0]);
                if(res.startsWith("GOTO:")) buka(Integer.parseInt(res.split(":")[1]));
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 30);
            contentArea.addView(item, lp);
        }
    }
}
