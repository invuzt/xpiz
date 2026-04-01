package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import static com.invuzt.xpiz.BrikStyle.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getHelloFromRust();

    private LinearLayout contentArea;
    private TextView btnProg, btnTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Root Abu-abu
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(CL_BG_OUTER);
        root.setPadding(30, 30, 30, 30);

        // Container Hitam Utama
        LinearLayout mainFrame = new LinearLayout(this);
        mainFrame.setOrientation(LinearLayout.VERTICAL);
        mainFrame.setBackground(round(CL_BLACK, 110));
        root.addView(mainFrame, new RelativeLayout.LayoutParams(-1, -1));

        // 1. Header (BRIK & Level)
        addBrikHeader(mainFrame);

        // 2. Scrollable Content
        ScrollView sv = new ScrollView(this);
        sv.setVerticalScrollBarEnabled(false);
        mainFrame.addView(sv);

        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(40, 20, 40, 250);
        sv.addView(contentArea);

        // 3. Bottom Navigation
        addBottomNav(root);

        drawTrainingUI();
        setContentView(root);
    }

    private void addBrikHeader(LinearLayout p) {
        RelativeLayout h = new RelativeLayout(this);
        h.setPadding(60, 70, 60, 40);
        
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
        
        // Kotak Putih (Rhythm Match)
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
            arrow.setAlpha(0.5f);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            card.addView(arrow, lp);

            contentArea.addView(card);
            space(contentArea, 15);
        }

        space(contentArea, 10);

        // SEQUENCE RUSH SECTION (3 Bagian)
        // Part 1: Header (Top Round)
        TextView rushH = new TextView(this);
        rushH.setText("Sequence rush          v");
        rushH.setPadding(60, 50, 60, 50);
        rushH.setBackground(roundCorners(CL_DARK_CARD, 80, 80, 20, 20));
        rushH.setTextColor(Color.WHITE);
        contentArea.addView(rushH);

        // Part 2: Scores (No Round)
        LinearLayout scoreBox = new LinearLayout(this);
        scoreBox.setPadding(60, 40, 60, 60);
        scoreBox.setBackgroundColor(CL_DARK_CARD);
        scoreBox.setWeightSum(2);
        
        scoreBox.addView(createScoreItem("Best score", "2,435"));
        scoreBox.addView(createScoreItem("Reaction speed", "319 ms"));
        contentArea.addView(scoreBox);

        // Part 3: Progress (Bottom Round)
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

        // START & VIEW LEADERS
        LinearLayout actions = new LinearLayout(this);
        actions.setWeightSum(2);
        
        TextView btnStart = createActionButton("START GAME", CL_ACCENT, Color.BLACK);
        TextView btnView = createActionButton("VIEW LEADERS", Color.TRANSPARENT, Color.WHITE);
        
        actions.addView(btnStart);
        actions.addView(btnView);
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
        b.setText(txt);
        b.setTextColor(tx);
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
        LinearLayout nav = new LinearLayout(this);
        nav.setBackground(round(Color.BLACK, 100));
        nav.setPadding(10, 10, 10, 10);
        nav.setGravity(Gravity.CENTER);

        btnProg = new TextView(this);
        btnProg.setText("PROGRESS");
        btnProg.setPadding(50, 30, 50, 30);
        btnProg.setTextColor(Color.WHITE);
        nav.addView(btnProg);

        btnTrain = new TextView(this);
        btnTrain.setText("TRAINING");
        btnTrain.setPadding(50, 30, 50, 30);
        btnTrain.setBackground(round(CL_ACCENT, 80));
        btnTrain.setTextColor(Color.BLACK);
        btnTrain.setTypeface(Typeface.DEFAULT_BOLD);
        nav.addView(btnTrain);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.setMargins(0, 0, 0, 60);
        root.addView(nav, lp);
    }
}
