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
        
        // --- PROSES IMMERSIVE STATUS BAR ---
        Window w = getWindow();
        // 1. Hilangkan batasan Status Bar agar layout naik ke mentok atas
        w.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        // 2. Set warna Status Bar jadi Hitam Pekat (Sama dengan App)
        w.setStatusBarColor(CL_BLACK);
        // 3. (Opsional) Jika background putih, gunakan FLAG_LIGHT_STATUS_BAR agar ikon jam hitam
        // Karena kita hitam, biarkan ikon tetap putih (default).

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(CL_BG_OUTER); // #081512

        LinearLayout mainFrame = new LinearLayout(this);
        mainFrame.setOrientation(LinearLayout.VERTICAL);
        mainFrame.setBackgroundColor(CL_BLACK); 
        root.addView(mainFrame, new RelativeLayout.LayoutParams(-1, -1));

        // HEADER: Sekarang kita naikkan padding top-nya agar teks BRIK berada tepat di bawah jam
        addBrikHeader(mainFrame);

        ScrollView sv = new ScrollView(this);
        sv.setVerticalScrollBarEnabled(false);
        mainFrame.addView(sv);

        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(50, 20, 50, 300); 
        sv.addView(contentArea);

        addBottomNav(root);
        drawTrainingUI();
        setContentView(root);
    }

    private void addBrikHeader(LinearLayout p) {
        RelativeLayout h = new RelativeLayout(this);
        // Padding top 130px agar teks BRIK mepet ke atas tapi tidak tertabrak jam sistem
        h.setPadding(60, 130, 60, 40);
        
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
        // ... (Kode UI Training Mas yang sudah mantap tetap sama)
        for(int i=0; i<2; i++) {
            RelativeLayout card = new RelativeLayout(this);
            card.setPadding(60, 60, 60, 60);
            card.setBackground(round(CL_WHITE, 100));
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
        // ... (sisanya sama)
    }
    // ... (Fungsi helper lainnya tetap sama)
}
