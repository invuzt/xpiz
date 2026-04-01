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
    private TextView tvLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(512, 512); // Layout no limits

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor(getSystemConfig("COLOR_GELAP")));

        tvLevel = new TextView(this); // Notif bubble
        tvLevel.setPadding(30, 10, 30, 10);
        tvLevel.setTypeface(null, Typeface.BOLD);
        // ... (setup header singkat)
        
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(1);
        contentArea.setPadding(40, 20, 40, 300);
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(contentArea);
        root.addView(scroll);

        navContainer = new LinearLayout(this);
        navContainer.setBackground(bulat(Color.BLACK, 150));
        navContainer.setPadding(20, 20, 20, 20);
        // ... (layout nav singkat)
        root.addView(navContainer);

        setContentView(root);
        buka(1);
    }

    void buka(int id) {
        contentArea.removeAllViews();
        String data = getContentFromRust(id);
        tvLevel.setText(getSystemConfig("NOTIF"));
        
        // Render Navbar
        navContainer.removeAllViews();
        String[] menus = getSystemConfig("NAVBAR").split("\\|");
        for(int i=0; i<menus.length; i++) {
            final int pageId = i+1;
            String[] stl = getStyleConfig(pageId).split("\\|");
            TextView b = new TextView(this);
            b.setText(menus[i]);
            b.setBackground(bulat(Color.parseColor(stl[0]), 100));
            b.setTextColor(Color.parseColor(stl[1]));
            b.setOnClickListener(v -> buka(pageId));
            navContainer.addView(b);
        }

        // Render Cards
        for(String s : data.split("\n")) {
            TextView c = new TextView(this);
            c.setText(s);
            c.setOnClickListener(v -> { handleTouch(s); tvLevel.setText(getSystemConfig("NOTIF")); });
            contentArea.addView(c);
        }
    }
}
