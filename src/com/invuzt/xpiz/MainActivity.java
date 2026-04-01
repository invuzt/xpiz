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
    private TextView btnProg, btnTrain, btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        w.setStatusBarColor(CL_BLACK);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(CL_BG_OUTER);

        LinearLayout mainFrame = new LinearLayout(this);
        mainFrame.setOrientation(LinearLayout.VERTICAL);
        mainFrame.setBackgroundColor(CL_BLACK); 
        root.addView(mainFrame, new RelativeLayout.LayoutParams(-1, -1));

        addHeader(mainFrame);

        ScrollView sv = new ScrollView(this);
        sv.setVerticalScrollBarEnabled(false);
        mainFrame.addView(sv);

        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        // Menggunakan Gembok Dimensi
        contentArea.setPadding(PAD_SCREEN, 20, PAD_SCREEN, 400); 
        sv.addView(contentArea);

        addBottomNav(root);
        drawTrainingUI();
        setContentView(root);
    }

    private void addHeader(LinearLayout p) {
        RelativeLayout h = new RelativeLayout(this);
        h.setPadding(PAD_SCREEN, PAD_HEADER_T, PAD_SCREEN, 40);
        TextView logo = new TextView(this);
        logo.setText(BRAND_NAME); // Otomatis XPIZ
        logo.setTextSize(26); 
        logo.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        logo.setTextColor(Color.WHITE);
        h.addView(logo);
        p.addView(h);
    }

    private void drawTrainingUI() {
        contentArea.removeAllViews();
        updateTabStyles(false, true, false);
        
        TextView title = new TextView(this);
        title.setText("Daily Training"); title.setTextColor(Color.WHITE); title.setTextSize(24);
        contentArea.addView(title);
        space(contentArea, 40);
        
        for(int i=0; i<2; i++) {
            RelativeLayout card = new RelativeLayout(this);
            card.setPadding(60, 60, 60, 60);
            card.setBackground(round(CL_WHITE, RADIUS_CARD));
            TextView t = new TextView(this);
            t.setText("Rhythm match"); t.setTextColor(Color.BLACK); t.setTextSize(18);
            card.addView(t);
            TextView arrow = new TextView(this);
            arrow.setText(">"); arrow.setTextColor(Color.GRAY);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            card.addView(arrow, lp);
            contentArea.addView(card);
            space(contentArea, 20);
        }
    }

    private void drawProgressUI() {
        contentArea.removeAllViews();
        updateTabStyles(true, false, false);
        contentArea.addView(createStatCard("Total XP", "4,250"));
        space(contentArea, 20);
        contentArea.addView(createStatCard("Rust Native", getHelloFromRust()));
    }

    private void drawAboutUI() {
        contentArea.removeAllViews();
        updateTabStyles(false, false, true);
        TextView title = new TextView(this);
        title.setText("About " + BRAND_NAME); title.setTextColor(Color.WHITE); title.setTextSize(24);
        contentArea.addView(title);
        space(contentArea, 40);
        contentArea.addView(BrikStyle.createDescription(contentArea, "XPIZ Mobile v1.0\nPonorogo Digital Works"));
    }

    private LinearLayout createStatCard(String label, String val) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(60, 60, 60, 60);
        card.setBackground(round(CL_DARK_CARD, 80));
        TextView t1 = new TextView(this); t1.setText(label); t1.setTextColor(Color.GRAY);
        card.addView(t1);
        TextView t2 = new TextView(this); t2.setText(val); t2.setTextColor(Color.WHITE); 
        t2.setTextSize(28); t2.setTypeface(Typeface.DEFAULT_BOLD);
        card.addView(t2);
        return card;
    }

    private void updateTabStyles(boolean isP, boolean isT, boolean isA) {
        btnProg.setBackground(isP ? round(CL_ACCENT, RADIUS_NAV) : null);
        btnProg.setTextColor(isP ? Color.BLACK : Color.WHITE);
        btnTrain.setBackground(isT ? round(CL_ACCENT, RADIUS_NAV) : null);
        btnTrain.setTextColor(isT ? Color.BLACK : Color.WHITE);
        btnAbout.setBackground(isA ? round(CL_ACCENT, RADIUS_NAV) : null);
        btnAbout.setTextColor(isA ? Color.BLACK : Color.WHITE);
    }

    private void addBottomNav(RelativeLayout root) {
        LinearLayout navWrap = new LinearLayout(this);
        navWrap.setBackground(round(Color.BLACK, 100));
        navWrap.setPadding(10, 10, 10, 10);
        navWrap.setGravity(Gravity.CENTER);

        btnProg = new TextView(this);
        btnProg.setText("PROGRESS");
        btnProg.setPadding(40, 30, 40, 30);
        btnProg.setOnClickListener(v -> drawProgressUI());
        navWrap.addView(btnProg);

        btnTrain = new TextView(this);
        btnTrain.setText("TRAINING");
        btnTrain.setPadding(40, 30, 40, 30);
        btnTrain.setOnClickListener(v -> drawTrainingUI());
        navWrap.addView(btnTrain);

        btnAbout = new TextView(this);
        btnAbout.setText(" ••• ");
        btnAbout.setPadding(40, 30, 40, 30);
        btnAbout.setOnClickListener(v -> drawAboutUI());
        navWrap.addView(btnAbout);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.setMargins(0, 0, 0, 60);
        root.addView(navWrap, lp);
    }
}
