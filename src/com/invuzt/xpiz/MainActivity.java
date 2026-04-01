package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getContentFromRust(int pageId);
    
    private LinearLayout contentArea;
    private TextView btnProg, btnTrain, btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(BrikStyle.BG);

        TextView logo = new TextView(this);
        logo.setText(BrikStyle.BRAND); 
        logo.setTextSize(24); 
        logo.setTextColor(BrikStyle.PUTIH);
        logo.setPadding(60, 100, 0, 0);
        root.addView(logo);

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
        t.setTextColor(BrikStyle.PUTIH); t.setTextSize(20);
        contentArea.addView(t);
    }

    void bukaProgress() {
        contentArea.removeAllViews();
        updateTombol(true, false, false);
        TextView t = new TextView(this);
        t.setText(getContentFromRust(2));
        t.setTextColor(BrikStyle.PUTIH); t.setTextSize(20);
        contentArea.addView(t);
    }

    void bukaAbout() {
        contentArea.removeAllViews();
        updateTombol(false, false, true);
        TextView t = new TextView(this);
        t.setText("XPIZ VERSION 1.0\n\nEngine: Rust 2021\nUI: Java Native");
        t.setTextColor(BrikStyle.PUTIH); t.setTextSize(18);
        contentArea.addView(t);
    }

    void updateTombol(boolean p, boolean t, boolean a) {
        if(btnProg == null) return;
        btnProg.setBackground(p ? BrikStyle.bulat(BrikStyle.AKSEN, 80) : null);
        btnProg.setTextColor(p ? Color.BLACK : BrikStyle.PUTIH);
        btnTrain.setBackground(t ? BrikStyle.bulat(BrikStyle.AKSEN, 80) : null);
        btnTrain.setTextColor(t ? Color.BLACK : BrikStyle.PUTIH);
        btnAbout.setBackground(a ? BrikStyle.bulat(BrikStyle.AKSEN, 80) : null);
        btnAbout.setTextColor(a ? Color.BLACK : BrikStyle.PUTIH);
    }

    void buatNavigasi(RelativeLayout root) {
        LinearLayout nav = new LinearLayout(this);
        nav.setBackground(BrikStyle.bulat(Color.BLACK, 100));
        nav.setPadding(20, 10, 20, 10);
        nav.setGravity(Gravity.CENTER);

        btnProg = new TextView(this); btnProg.setText(" PROGRESS ");
        btnProg.setPadding(40, 30, 40, 30);
        btnProg.setOnClickListener(v -> bukaProgress());
        nav.addView(btnProg);

        btnTrain = new TextView(this); btnTrain.setText(" TRAINING ");
        btnTrain.setPadding(40, 30, 40, 30);
        btnTrain.setOnClickListener(v -> bukaTraining());
        nav.addView(btnTrain);

        btnAbout = new TextView(this); btnAbout.setText(" ••• ");
        btnAbout.setPadding(40, 30, 40, 30);
        btnAbout.setOnClickListener(v -> bukaAbout());
        nav.addView(btnAbout);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(0, 0, 0, 100);
        root.addView(nav, lp);
    }
}
