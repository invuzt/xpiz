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
    private TextView btnProg, btnTrain;

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

        addBrikHeader(mainFrame);

        ScrollView sv = new ScrollView(this);
        sv.setVerticalScrollBarEnabled(false);
        mainFrame.addView(sv);

        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(50, 20, 50, 400); 
        sv.addView(contentArea);

        addBottomNav(root);

        // Default awal: Halaman Training
        drawTrainingUI();
        setContentView(root);
    }

    private void addBrikHeader(LinearLayout p) {
        RelativeLayout h = new RelativeLayout(this);
        h.setPadding(60, 130, 60, 40);
        TextView logo = new TextView(this);
        logo.setText("BRIK®"); logo.setTextSize(26); logo.setTextColor(Color.WHITE);
        h.addView(logo);
        p.addView(h);
    }

    // HALAMAN TRAINING (Isi lama)
    private void drawTrainingUI() {
        contentArea.removeAllViews();
        updateTabStyles(false); // Highlight tombol Training
        
        TextView title = new TextView(this);
        title.setText("Daily Training"); title.setTextColor(Color.WHITE); title.setTextSize(24);
        contentArea.addView(title);
        space(contentArea, 40);
        
        // Contoh kartu putih dari BrikStyle
        RelativeLayout card = new RelativeLayout(this);
        card.setPadding(60, 60, 60, 60);
        card.setBackground(round(CL_WHITE, 100));
        TextView t = new TextView(this);
        t.setText("Rhythm match"); t.setTextColor(Color.BLACK);
        card.addView(t);
        contentArea.addView(card);
    }

    // HALAMAN PROGRESS (Isi Baru)
    private void drawProgressUI() {
        contentArea.removeAllViews();
        updateTabStyles(true); // Highlight tombol Progress
        
        TextView title = new TextView(this);
        title.setText("Your Statistics");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        contentArea.addView(title);
        space(contentArea, 40);

        // Gunakan komponen stat card dari BrikStyle agar konsisten
        contentArea.addView(createStatCard(contentArea, "Total Games", "128"));
        space(contentArea, 20);
        contentArea.addView(createStatCard(contentArea, "Win Rate", "84%"));
        space(contentArea, 20);
        contentArea.addView(createStatCard(contentArea, "Rust Logic", getHelloFromRust()));
    }

    private void updateTabStyles(boolean isProgress) {
        if (isProgress) {
            btnProg.setBackground(round(CL_ACCENT, 80));
            btnProg.setTextColor(Color.BLACK);
            btnTrain.setBackground(null);
            btnTrain.setTextColor(Color.WHITE);
        } else {
            btnTrain.setBackground(round(CL_ACCENT, 80));
            btnTrain.setTextColor(Color.BLACK);
            btnProg.setBackground(null);
            btnProg.setTextColor(Color.WHITE);
        }
    }

    private void addBottomNav(RelativeLayout root) {
        LinearLayout navWrap = new LinearLayout(this);
        navWrap.setBackground(round(Color.BLACK, 100));
        navWrap.setPadding(10, 10, 10, 10);

        btnProg = new TextView(this);
        btnProg.setText("PROGRESS");
        btnProg.setPadding(50, 30, 50, 30);
        btnProg.setOnClickListener(v -> drawProgressUI());
        navWrap.addView(btnProg);

        btnTrain = new TextView(this);
        btnTrain.setText("TRAINING");
        btnTrain.setPadding(50, 30, 50, 30);
        btnTrain.setOnClickListener(v -> drawTrainingUI());
        navWrap.addView(btnTrain);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.setMargins(0, 0, 0, 60);
        root.addView(navWrap, lp);
    }

    private View createStatCard(ViewGroup p, String t, String v) {
        return BrikStyle.createStatCard(p, t, v);
    }
}
