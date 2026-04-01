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

    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. Layout Luar (F5F5F5)
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(CL_BG_OUTER);
        root.setPadding(20, 20, 20, 20);

        // 2. Container Hitam (Rounding Besar)
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setBackground(drawRound(CL_BG_INNER, 120));
        root.addView(inner, new RelativeLayout.LayoutParams(-1, -1));

        // 3. Header Shared (BRIK & LEVEL)
        addHeader(inner);

        // 4. Scrollable Content
        ScrollView sv = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(50, 20, 50, 250);
        sv.addView(content);
        inner.addView(sv);

        // Tampilan Training (Sesuai Gambar)
        drawTrainingUI();

        setContentView(root);
    }

    private void addHeader(LinearLayout p) {
        RelativeLayout h = new RelativeLayout(this);
        h.setPadding(60, 60, 60, 20);
        
        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(24);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        logo.setTextColor(Color.WHITE);
        h.addView(logo);

        TextView lvl = new TextView(this);
        lvl.setText("71 LEVEL");
        lvl.setPadding(30, 10, 30, 10);
        lvl.setBackground(drawRound(CL_ACCENT, 40));
        lvl.setTextColor(Color.BLACK);
        lvl.setTypeface(Typeface.DEFAULT_BOLD);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        h.addView(lvl, lp);
        
        p.addView(h);
    }

    private void drawTrainingUI() {
        content.removeAllViews();
        
        // Kotak Putih (Rhythm Match)
        for(int i=0; i<2; i++) {
            TextView c = new TextView(this);
            c.setText("Rhythm match");
            c.setPadding(60, 50, 60, 50);
            c.setBackground(drawRound(CL_CARD_W, 100));
            c.setTextColor(Color.BLACK);
            content.addView(c);
            addSpace(content, 20);
        }

        addSpace(content, 30);

        // Sequence Rush Section (Sesuai Gambar)
        TextView rushH = new TextView(this);
        rushH.setText("Sequence rush      v");
        rushH.setPadding(60, 40, 60, 40);
        rushH.setBackground(drawHalfRound(Color.parseColor("#0A0A0A"), true));
        rushH.setTextColor(Color.WHITE);
        content.addView(rushH);

        LinearLayout score = new LinearLayout(this);
        score.setPadding(60, 40, 60, 60);
        score.setBackground(drawHalfRound(Color.parseColor("#0A0A0A"), false));
        
        TextView scoreVal = new TextView(this);
        scoreVal.setText("2,435\nBest Score");
        scoreVal.setTextColor(Color.WHITE);
        scoreVal.setTextSize(20);
        score.addView(scoreVal);
        content.addView(score);

        // Data dari Rust (Sebagai Footer Training)
        addSpace(content, 50);
        TextView rust = new TextView(this);
        rust.setText("Rust Logic: " + getHelloFromRust());
        rust.setTextColor(CL_ACCENT);
        rust.setGravity(Gravity.CENTER);
        content.addView(rust);
    }
}
