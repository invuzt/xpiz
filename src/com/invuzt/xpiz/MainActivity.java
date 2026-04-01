package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import static com.invuzt.xpiz.BrikStyle.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getContentFromRust(int id);
    private LinearLayout contentArea;
    private TextView bProg, bTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#F7F9F8")); // Background luar agak putih/abu

        // 1. Container Utama (Gelap)
        LinearLayout mainCard = new LinearLayout(this);
        mainCard.setOrientation(LinearLayout.VERTICAL);
        mainCard.setBackground(bulat(GELAP, 120));
        mainCard.setPadding(40, 80, 40, 80);
        
        RelativeLayout.LayoutParams mainParams = new RelativeLayout.LayoutParams(-1, -1);
        mainParams.setMargins(20, 20, 20, 250); // Kasih ruang buat navbar bawah
        root.addView(mainCard, mainParams);

        // Logo
        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(28);
        logo.setTextColor(PUTIH);
        logo.setPadding(40, 0, 0, 40);
        mainCard.addView(logo);

        // Area Konten (Hasil dari Rust)
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        mainCard.addView(contentArea);

        // 2. Navbar Bawah (Hitam)
        LinearLayout nav = new LinearLayout(this);
        nav.setBackground(bulat(Color.BLACK, 150));
        nav.setPadding(20, 20, 20, 20);
        nav.setGravity(Gravity.CENTER);

        bProg = buatNavBtn(" PROGRESS ");
        bTrain = buatNavBtn(" TRAINING ");
        
        bProg.setOnClickListener(v -> buka(2));
        bTrain.setOnClickListener(v -> buka(1));

        nav.addView(bProg);
        nav.addView(bTrain);

        RelativeLayout.LayoutParams navP = new RelativeLayout.LayoutParams(-2, -2);
        navP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        navP.addRule(RelativeLayout.CENTER_HORIZONTAL);
        navP.setMargins(0, 0, 0, 60);
        root.addView(nav, navP);

        setContentView(root);
        buka(1);
    }

    private TextView buatNavBtn(String txt) {
        TextView tv = new TextView(this);
        tv.setText(txt);
        tv.setPadding(60, 35, 60, 35);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    void buka(int id) {
        contentArea.removeAllViews();
        // Update Style Tombol Nav
        bProg.setBackground(id == 2 ? bulat(AKSEN, 100) : null);
        bProg.setTextColor(id == 2 ? Color.BLACK : Color.GRAY);
        bTrain.setBackground(id == 1 ? bulat(AKSEN, 100) : null);
        bTrain.setTextColor(id == 1 ? Color.BLACK : Color.GRAY);

        // Card dari Rust
        TextView card = new TextView(this);
        card.setText(getContentFromRust(id));
        card.setBackground(card(PUTIH, Color.BLACK, 3)); // Card putih dengan border hitam
        card.setPadding(60, 60, 60, 60);
        card.setTextColor(Color.BLACK);
        card.setTextSize(18);
        
        contentArea.addView(card);
    }
}
