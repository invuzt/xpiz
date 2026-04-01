package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import static com.invuzt.xpiz.BrikStyle.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getHelloFromRust();

    private LinearLayout contentArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. ROOT FULLSCREEN (Hapus Padding)
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(CL_BG_OUTER);

        // 2. MAIN FRAME (Tanpa Rounding di Pinggir Luar agar Fullscreen)
        LinearLayout mainFrame = new LinearLayout(this);
        mainFrame.setOrientation(LinearLayout.VERTICAL);
        // Kita tetap beri warna hitam pekat
        mainFrame.setBackgroundColor(CL_BLACK); 
        root.addView(mainFrame, new RelativeLayout.LayoutParams(-1, -1));

        // 3. HEADER (Sesuaikan Padding agar tidak terlalu mepet status bar)
        addBrikHeader(mainFrame);

        // 4. SCROLL CONTENT
        ScrollView sv = new ScrollView(this);
        sv.setVerticalScrollBarEnabled(false);
        mainFrame.addView(sv);

        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        // Beri padding samping agar konten tetap rapi di dalam
        contentArea.setPadding(50, 20, 50, 300); 
        sv.addView(contentArea);

        // 5. BOTTOM NAV (Melayang Tetap Sama)
        addBottomNav(root);

        drawTrainingUI();
        setContentView(root);
    }

    private void addBrikHeader(LinearLayout p) {
        RelativeLayout h = new RelativeLayout(this);
        // Tambah padding top untuk notch/status bar (80-100px)
        h.setPadding(60, 100, 60, 40);
        
        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(26);
        logo.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        logo.setTextColor(Color.WHITE);
        h.addView(logo);

        TextView lvl = new TextView(this);
        lvl.setText("71 LEVEL");
        lvl.setPadding(35, 15, 35, 15);
        lvl.setBackground(round(CL_ACCENT, 45));
        lvl.setTextColor(Color.BLACK);
        lvl.setTextSize(12);
        lvl.setTypeface(Typeface.DEFAULT_BOLD);
        
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        h.addView(lvl, lp);
        p.addView(h);
    }

    private void drawTrainingUI() {
        contentArea.removeAllViews();
        
        // Kartu Putih (Tetap dengan Rounding 100)
        for(int i=0; i<2; i++) {
            RelativeLayout card = new RelativeLayout(this);
            card.setPadding(60, 60, 60, 60);
            card.setBackground(round(CL_WHITE, 100));
            
            TextView t = new TextView(this);
            t.setText("Rhythm match");
            t.setTextColor(Color.BLACK);
            t.setTextSize(18);
            card.addView(t);

            TextView arrow = new TextView(this);
            arrow.setText(">");
            arrow.setTextColor(Color.GRAY);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            card.addView(arrow, lp);

            contentArea.addView(card);
            space(contentArea, 20);
        }

        space(contentArea, 10);

        // SEQUENCE RUSH SECTION
        TextView rushH = new TextView(this);
        rushH.setText("Sequence rush          v");
        rushH.setPadding(60, 50, 60, 50);
        rushH.setBackground(roundCorners(CL_DARK_CARD, 80, 80, 20, 20));
        rushH.setTextColor(Color.WHITE);
        contentArea.addView(rushH);

        LinearLayout scoreBox = new LinearLayout(this);
        scoreBox.setPadding(60, 40, 60, 60);
        scoreBox.setBackgroundColor(CL_DARK_CARD);
        scoreBox.setWeightSum(2);
        scoreBox.addView(createScoreItem("Best score", "2,435"));
        scoreBox.addView(createScoreItem("Reaction speed", "319 ms"));
        contentArea.addView(scoreBox);

        LinearLayout progBox = new LinearLayout(this);
        progBox.setOrientation(LinearLayout.VERTICAL);
        progBox.setPadding(60, 40, 60, 70);
        progBox.setBackground(roundCorners(CL_DARK_CARD, 20, 20, 80, 80));
        
        TextView progTitle = new TextView(this);
        progTitle.setText("Your progress");
        progTitle.setTextColor(Color.WHITE);
        progBox.addView(progTitle);
        contentArea.addView(progBox);

        space(contentArea, 40);

        // TOMBOL AKSI
        LinearLayout actions = new LinearLayout(this);
        actions.setWeightSum(2);
        actions.addView(createActionButton("START GAME", CL_ACCENT, Color.BLACK));
        actions.addView(createActionButton("VIEW LEADERS", Color.TRANSPARENT, Color.WHITE));
        contentArea.addView(actions);
    }

    private View createScoreItem(String label, String val) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));
        TextView t1 = new TextView(this);
        t1.setText(label); t1.setTextColor(Color.GRAY); t1.setTextSize(12);
        l.addView(t1);
        TextView t2 = new TextView(this);
        t2.setText(val); t2.setTextColor(Color.WHITE); t2.setTextSize(26);
        t2.setTypeface(Typeface.DEFAULT_BOLD);
        l.addView(t2);
        return l;
    }

    private TextView createActionButton(String txt, int bg, int tx) {
        TextView b = new TextView(this);
        b.setText(txt); b.setTextColor(tx);
        b.setGravity(Gravity.CENTER);
        b.setPadding(0, 50, 0, 50);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(10, 0, 10, 0);
        b.setLayoutParams(lp);
        
        if(bg == Color.TRANSPARENT) {
            GradientDrawable gd = new GradientDrawable();
            gd.setStroke(3, Color.GRAY);
            gd.setCornerRadius(100);
            b.setBackground(gd);
        } else {
            b.setBackground(round(bg, 100));
        }
        return b;
    }

    private void addBottomNav(RelativeLayout root) {
        LinearLayout navWrap = new LinearLayout(this);
        navWrap.setBackground(round(Color.BLACK, 100));
        navWrap.setPadding(10, 10, 10, 10);
        navWrap.setGravity(Gravity.CENTER);

        TextView p = new TextView(this);
        p.setText("PROGRESS");
        p.setPadding(50, 30, 50, 30);
        p.setTextColor(Color.WHITE);
        navWrap.addView(p);

        TextView t = new TextView(this);
        t.setText("TRAINING");
        t.setPadding(50, 30, 50, 30);
        t.setBackground(round(CL_ACCENT, 80));
        t.setTextColor(Color.BLACK);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        navWrap.addView(t);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.setMargins(0, 0, 0, 60);
        root.addView(navWrap, lp);
    }
}
