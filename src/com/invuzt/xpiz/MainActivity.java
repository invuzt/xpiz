package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import static com.invuzt.xpiz.BrikStyle.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getContentFromRust(int pageId);
    
    private LinearLayout contentArea;
    private TextView btnProg, btnTrain, btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(BG);

        TextView logo = new TextView(this);
        logo.setId(View.generateViewId());
        logo.setText("XPIZ®");
        logo.setTextSize(26);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setTextColor(PUTIH);
        logo.setPadding(60, 120, 0, 40);
        root.addView(logo);

        LinearLayout nav = buatNavigasi();
        nav.setId(View.generateViewId());
        RelativeLayout.LayoutParams navParams = new RelativeLayout.LayoutParams(-2, -2);
        navParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        navParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        navParams.setMargins(0, 0, 0, 100);
        root.addView(nav, navParams);

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
        n.setBackground(bulat(Color.BLACK, 100));
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
        t.setTextColor(PUTIH);
        t.setTextSize(19);
        t.setLineSpacing(10, 1.2f);
        
        if (id == 3) {
            t.setText("XPIZ SYSTEM\n\nStatus: Online\nEngine: Rust Core\nUI: Java Dynamic");
        } else {
            t.setText(getContentFromRust(id));
        }
        contentArea.addView(t);
    }

    void updateTombol(int activeId) {
        btnProg.setBackground(activeId == 2 ? bulat(AKSEN, 80) : null);
        btnProg.setTextColor(activeId == 2 ? Color.BLACK : PUTIH);
        
        btnTrain.setBackground(activeId == 1 ? bulat(AKSEN, 80) : null);
        btnTrain.setTextColor(activeId == 1 ? Color.BLACK : PUTIH);
        
        btnAbout.setBackground(activeId == 3 ? bulat(AKSEN, 80) : null);
        btnAbout.setTextColor(activeId == 3 ? Color.BLACK : PUTIH);
    }
    
    void bukaTraining() { bukaHalaman(1); }
}
