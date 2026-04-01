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
        
        // Root Layout pakai warna BG dari BrikStyle
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(BG);

        // Area Konten
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(60, 250, 60, 0);
        root.addView(contentArea);

        buatNavigasi(root);
        bukaTraining(); 
        setContentView(root);
    }

    void bukaTraining() {
        contentArea.removeAllViews();
        updateTombol(false, true, false);
        TextView t = new TextView(this);
        t.setText(getContentFromRust(1));
        t.setTextColor(PUTIH); t.setTextSize(18); // Pakai warna PUTIH BrikStyle
        contentArea.addView(t);
    }

    void updateTombol(boolean p, boolean t, boolean a) {
        // Ini yang bikin efek "CSS" (Tombol membulat & ganti warna)
        btnProg.setBackground(p ? bulat(AKSEN, 80) : null);
        btnProg.setTextColor(p ? Color.BLACK : PUTIH);
        
        btnTrain.setBackground(t ? bulat(AKSEN, 80) : null);
        btnTrain.setTextColor(t ? Color.BLACK : PUTIH);
        
        btnAbout.setBackground(a ? bulat(AKSEN, 80) : null);
        btnAbout.setTextColor(a ? Color.BLACK : PUTIH);
    }

    void buatNavigasi(RelativeLayout root) {
        LinearLayout nav = new LinearLayout(this);
        nav.setBackground(bulat(Color.BLACK, 100)); // Navigasi Hitam Bulat
        nav.setPadding(10, 10, 10, 10);
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
        lp.setMargins(0, 0, 0, 80);
        root.addView(nav, lp);
    }
    
    // Tambahkan method bukaProgress & bukaAbout jika belum ada...
    void bukaProgress() { /* sama seperti bukaTraining tapi ID 2 */ }
    void bukaAbout() { /* teks manual tentang XPIZ */ }
}
