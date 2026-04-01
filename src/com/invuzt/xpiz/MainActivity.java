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
    
    // --- CSS / STYLE LANGSUNG DI SINI ---
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
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Style.BG);

        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(60, 300, 60, 300);
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(contentArea);
        root.addView(scroll);

        buatNavigasi(root);
        setContentView(root);
        bukaTraining();
    }

    void bukaTraining() {
        contentArea.removeAllViews();
        updateTombol(false, true, false);
        TextView t = new TextView(this);
        t.setText(getContentFromRust(1));
        t.setTextColor(Style.PUTIH); t.setTextSize(20);
        contentArea.addView(t);
    }

    void updateTombol(boolean p, boolean t, boolean a) {
        if(btnProg == null) return;
        btnProg.setBackground(p ? Style.bulat(Style.AKSEN, 80) : null);
        btnProg.setTextColor(p ? Color.BLACK : Style.PUTIH);
        btnTrain.setBackground(t ? Style.bulat(Style.AKSEN, 80) : null);
        btnTrain.setTextColor(t ? Color.BLACK : Style.PUTIH);
        btnAbout.setBackground(a ? Style.bulat(Style.AKSEN, 80) : null);
        btnAbout.setTextColor(a ? Color.BLACK : Style.PUTIH);
    }

    void buatNavigasi(RelativeLayout root) {
        LinearLayout nav = new LinearLayout(this);
        nav.setBackground(Style.bulat(Color.BLACK, 100));
        nav.setPadding(20, 10, 20, 10);
        nav.setGravity(Gravity.CENTER);

        btnProg = new TextView(this); btnProg.setText(" PROGRESS ");
        btnProg.setPadding(40, 30, 40, 30);
        btnProg.setOnClickListener(v -> {
            contentArea.removeAllViews();
            updateTombol(true, false, false);
            TextView t = new TextView(this);
            t.setText(getContentFromRust(2));
            t.setTextColor(Style.PUTIH); t.setTextSize(20);
            contentArea.addView(t);
        });
        nav.addView(btnProg);

        btnTrain = new TextView(this); btnTrain.setText(" TRAINING ");
        btnTrain.setPadding(40, 30, 40, 30);
        btnTrain.setOnClickListener(v -> bukaTraining());
        nav.addView(btnTrain);

        btnAbout = new TextView(this); btnAbout.setText(" ••• ");
        btnAbout.setPadding(40, 30, 40, 30);
        btnAbout.setOnClickListener(v -> {
            contentArea.removeAllViews();
            updateTombol(false, false, true);
            TextView t = new TextView(this);
            t.setText("XPIZ VERSION 1.0\n\nEngine: Rust\nUI: All-in-One");
            t.setTextColor(Style.PUTIH); t.setTextSize(18);
            contentArea.addView(t);
        });
        nav.addView(btnAbout);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(0, 0, 0, 100);
        root.addView(nav, lp);
    }
}
