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
    private TextView btnProg, btnTrain, btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // PENTING: Inisialisasi tema dulu
        BrikStyle.updateTheme(this);
        super.onCreate(savedInstanceState);
        
        Window w = getWindow();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        
        // Jika Light Mode, buat ikon status bar jadi gelap (biar kelihatan)
        if (CL_TEXT_PRIMARY == Color.BLACK) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        
        w.getDecorView().setSystemUiVisibility(flags);
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
        contentArea.setPadding(PAD_SCREEN, 20, PAD_SCREEN, 400); 
        sv.addView(contentArea);

        addBottomNav(root);
        drawTrainingUI();
        setContentView(root);
    }

    private void addHeader(LinearLayout p) {
        RelativeLayout h = new RelativeLayout(this);
        h.setPadding(PAD_SCREEN, 130, PAD_SCREEN, 40);
        TextView logo = new TextView(this);
        logo.setText(BRAND_NAME); logo.setTextSize(26); 
        logo.setTextColor(CL_TEXT_PRIMARY); // Adaptif
        h.addView(logo);
        p.addView(h);
    }

    private void drawTrainingUI() {
        contentArea.removeAllViews();
        updateTabStyles(false, true, false);
        contentArea.addView(createWhiteCard(contentArea, "Rhythm match"));
        space(contentArea, 20);
        contentArea.addView(createWhiteCard(contentArea, "Sequence rush"));
    }

    private void drawProgressUI() {
        contentArea.removeAllViews();
        updateTabStyles(true, false, false);
        contentArea.addView(createStatCard(contentArea, "Total XP", "4,250"));
        space(contentArea, 20);
        contentArea.addView(createStatCard(contentArea, "Rust Native", getHelloFromRust()));
    }

    private void drawAboutUI() {
        contentArea.removeAllViews();
        updateTabStyles(false, false, true);
        TextView title = new TextView(this);
        title.setText("About " + BRAND_NAME); 
        title.setTextColor(CL_TEXT_PRIMARY); title.setTextSize(24);
        contentArea.addView(title);
        space(contentArea, 40);
        TextView desc = new TextView(this);
        desc.setText("XPIZ Mobile v1.0\nPonorogo Digital Works");
        desc.setTextColor(Color.GRAY);
        contentArea.addView(desc);
    }

    private void updateTabStyles(boolean isP, boolean isT, boolean isA) {
        btnProg.setBackground(isP ? round(CL_ACCENT, RADIUS_NAV) : null);
        btnProg.setTextColor(isP ? (CL_TEXT_PRIMARY == Color.BLACK ? Color.WHITE : Color.BLACK) : CL_TEXT_PRIMARY);
        
        btnTrain.setBackground(isT ? round(CL_ACCENT, RADIUS_NAV) : null);
        btnTrain.setTextColor(isT ? (CL_TEXT_PRIMARY == Color.BLACK ? Color.WHITE : Color.BLACK) : CL_TEXT_PRIMARY);
        
        btnAbout.setBackground(isA ? round(CL_ACCENT, RADIUS_NAV) : null);
        btnAbout.setTextColor(isA ? (CL_TEXT_PRIMARY == Color.BLACK ? Color.WHITE : Color.BLACK) : CL_TEXT_PRIMARY);
    }

    private void addBottomNav(RelativeLayout root) {
        LinearLayout navWrap = new LinearLayout(this);
        // Navigasi tetap hitam pekat biar kontras, atau bisa Mas ganti CL_BLACK
        navWrap.setBackground(round(Color.BLACK, 100));
        navWrap.setPadding(10, 10, 10, 10);
        navWrap.setGravity(Gravity.CENTER);

        btnProg = new TextView(this); btnProg.setText("PROGRESS");
        btnProg.setPadding(40, 30, 40, 30);
        btnProg.setOnClickListener(v -> drawProgressUI());
        navWrap.addView(btnProg);

        btnTrain = new TextView(this); btnTrain.setText("TRAINING");
        btnTrain.setPadding(40, 30, 40, 30);
        btnTrain.setOnClickListener(v -> drawTrainingUI());
        navWrap.addView(btnTrain);

        btnAbout = new TextView(this); btnAbout.setText(" ••• ");
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
