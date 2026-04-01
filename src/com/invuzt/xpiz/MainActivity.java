package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import java.io.File;
import static com.invuzt.xpiz.BrikStyle.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getSystemConfig(String k);
    private native String getStyleConfig(int id);
    private native String getContentFromRust(int id);
    private native String handleTouch(String tag, String val);

    private LinearLayout contentArea, navContainer;
    private TextView tvLevel, tvLogo;
    private EditText globalInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // PASTIKAN FOLDER INTERNAL TERSEDIA UNTUK RUST
        File filesDir = getFilesDir();
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }

        getWindow().setFlags(512, 512);
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor(getSystemConfig("COLOR_GELAP")));

        // 1. Header
        RelativeLayout header = new RelativeLayout(this);
        header.setId(View.generateViewId());
        header.setPadding(60, 150, 60, 40);
        tvLogo = new TextView(this);
        tvLogo.setText(getSystemConfig("LOGO"));
        tvLogo.setTextSize(28);
        tvLogo.setTypeface(null, Typeface.BOLD);
        tvLogo.setTextColor(Color.WHITE);
        header.addView(tvLogo);

        tvLevel = new TextView(this);
        tvLevel.setText(getSystemConfig("NOTIF"));
        tvLevel.setPadding(35, 12, 35, 12);
        tvLevel.setBackground(bulat(Color.parseColor("#D0C9FF"), 50));
        tvLevel.setTextColor(Color.BLACK);
        tvLevel.setTypeface(null, Typeface.BOLD);
        RelativeLayout.LayoutParams lpL = new RelativeLayout.LayoutParams(-2,-2);
        lpL.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(tvLevel, lpL);
        root.addView(header);

        // 2. Global Input
        globalInput = new EditText(this);
        globalInput.setId(View.generateViewId());
        globalInput.setHint("Train AI...");
        globalInput.setHintTextColor(Color.GRAY);
        globalInput.setTextColor(Color.CYAN);
        globalInput.setSingleLine(true);
        globalInput.setImeOptions(EditorInfo.IME_ACTION_SEND);
        globalInput.setBackground(card(Color.parseColor("#1A1A1A"), 0, 0));
        globalInput.setPadding(50, 40, 50, 40);
        globalInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                handleTouch("SEND_INPUT", globalInput.getText().toString());
                globalInput.setText("");
                buka(1);
                return true;
            }
            return false;
        });
        RelativeLayout.LayoutParams lpI = new RelativeLayout.LayoutParams(-1, -2);
        lpI.addRule(RelativeLayout.BELOW, header.getId());
        lpI.setMargins(40, 20, 40, 20);
        root.addView(globalInput, lpI);

        // 3. Content Scroll
        ScrollView scroll = new ScrollView(this);
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(1);
        contentArea.setPadding(40, 20, 40, 400);
        scroll.addView(contentArea);
        RelativeLayout.LayoutParams lpS = new RelativeLayout.LayoutParams(-1, -1);
        lpS.addRule(RelativeLayout.BELOW, globalInput.getId());
        root.addView(scroll, lpS);

        // 4. Navbar
        HorizontalScrollView ns = new HorizontalScrollView(this);
        ns.setBackground(bulat(Color.BLACK, 150));
        navContainer = new LinearLayout(this);
        navContainer.setPadding(20, 20, 20, 20);
        ns.addView(navContainer);
        RelativeLayout.LayoutParams lpN = new RelativeLayout.LayoutParams(-2, -2);
        lpN.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpN.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lpN.setMargins(0, 0, 0, 80);
        root.addView(ns, lpN);

        setContentView(root);
        buka(1);
    }

    void refreshNavbar(int activeId) {
        navContainer.removeAllViews();
        String[] menus = getSystemConfig("NAVBAR").split("\\|");
        for(int i=0; i<menus.length; i++) {
            final int pid = i+1;
            String[] stl = getStyleConfig(pid).split("\\|");
            TextView b = new TextView(this);
            b.setText(" "+menus[i]+" ");
            b.setPadding(45, 25, 45, 25);
            b.setBackground(bulat(Color.parseColor(stl[0]), 100));
            b.setTextColor(Color.parseColor(stl[1]));
            b.setOnClickListener(v -> buka(pid));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.setMargins(10, 0, 10, 0);
            navContainer.addView(b, lp);
        }
    }

    void buka(int id) {
        contentArea.removeAllViews();
        String data = getContentFromRust(id);
        tvLevel.setText(getSystemConfig("NOTIF"));
        refreshNavbar(id);

        for (String line : data.split("\n")) {
            String labelTxt = line.split("\\|")[0];
            TextView card = new TextView(this);
            card.setText(labelTxt);
            card.setBackground(card(Color.parseColor("#1A1A1A"), 0, 0));
            card.setPadding(60, 60, 60, 60);
            card.setTextColor(Color.WHITE);
            card.setTypeface(null, Typeface.BOLD);
            card.setOnClickListener(v -> {
                handleTouch(labelTxt, "");
                buka(id); // Refresh state
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 25);
            contentArea.addView(card, lp);
        }
    }
}
