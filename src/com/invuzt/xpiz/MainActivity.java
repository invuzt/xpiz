package com.invuzt.xpiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.text.InputType; // INI YANG TADI KURANG, MAS!

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private final String CL_BG = "#FFFFFF"; 
    private final String CL_CARD = "#071D18"; 
    private final String CL_ACCENT = "#D0C9FF"; 
    private final String CL_TEXT_ON_CARD = "#FFFFFF";
    private final String CL_TEXT_ON_BG = "#000000";

    private TextView totalView, aiNotif, txtInput;
    private int totalBelanja = 0, currentProgress = 79;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RelativeLayout finalLayout = new RelativeLayout(this);
        finalLayout.setBackgroundColor(Color.parseColor(CL_BG));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 0);

        // Header BRIK
        RelativeLayout header = new RelativeLayout(this);
        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(32);
        logo.setTextColor(Color.BLACK);
        header.addView(logo);
        root.addView(header);

        // Card Welcome
        addSpacer(root, 50);
        TextView welcome = new TextView(this);
        welcome.setText("Odfiz,\nselamat datang kembali");
        welcome.setTextSize(26);
        welcome.setPadding(60, 60, 60, 60);
        welcome.setTextColor(Color.WHITE);
        welcome.setBackground(createCurvedDrawable(Color.parseColor(CL_CARD), 80));
        root.addView(welcome);

        // Stat Row (Omset)
        addSpacer(root, 40);
        LinearLayout statsRow = new LinearLayout(this);
        LinearLayout cardOmset = createStatCard("Omset Hari Ini", "Rp 7.593", CL_ACCENT);
        statsRow.addView(cardOmset, new LinearLayout.LayoutParams(0, -2, 1.0f));
        root.addView(statsRow);

        // Input Area
        addSpacer(root, 40);
        txtInput = new TextView(this);
        txtInput.setText("Tap layar untuk input...");
        txtInput.setTextSize(20);
        root.addView(txtInput);

        finalLayout.addView(root);
        addHiddenInput(finalLayout);

        setContentView(finalLayout);
    }

    private GradientDrawable createCurvedDrawable(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

    private LinearLayout createStatCard(String title, String val, String valColor) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(40, 40, 40, 40);
        card.setBackground(createCurvedDrawable(Color.parseColor(CL_CARD), 80));
        TextView tvV = new TextView(this);
        tvV.setText(val);
        tvV.setTextColor(Color.parseColor(valColor));
        tvV.setTextSize(30);
        card.addView(tvV);
        return card;
    }

    private void addSpacer(LinearLayout v, int h) {
        View s = new View(this);
        v.addView(s, new LinearLayout.LayoutParams(-1, h));
    }

    private void addHiddenInput(ViewGroup vg) {
        EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setImeOptions(EditorInfo.IME_ACTION_SEND);
        vg.addView(et, new ViewGroup.LayoutParams(1, 1));
        vg.setOnClickListener(v -> { et.requestFocus(); 
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        });
        et.setOnEditorActionListener((v, id, ev) -> {
            txtInput.setText(et.getText().toString());
            et.setText("");
            return true;
        });
    }
}
