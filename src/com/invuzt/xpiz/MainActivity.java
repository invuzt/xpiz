package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.text.InputType;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private final String CL_BG = "#FFFFFF"; 
    private final String CL_CARD = "#071D18"; 
    private final String CL_ACCENT = "#D0C9FF"; 

    private TextView txtInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RelativeLayout rootWrap = new RelativeLayout(this);
        rootWrap.setBackgroundColor(Color.parseColor(CL_BG));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(50, 80, 50, 0);

        TextView logo = new TextView(this);
        logo.setText("BRIK®");
        logo.setTextSize(30);
        logo.setTextColor(Color.BLACK);
        logo.setTypeface(Typeface.DEFAULT_BOLD);
        content.addView(logo);

        addSpacer(content, 60);
        TextView welcome = new TextView(this);
        welcome.setText("Odfiz,\nselamat datang kembali");
        welcome.setTextSize(24);
        welcome.setPadding(60, 70, 60, 70);
        welcome.setTextColor(Color.WHITE);
        welcome.setBackground(createRounded(Color.parseColor(CL_CARD), 90));
        content.addView(welcome);

        addSpacer(content, 30);
        LinearLayout cardStat = new LinearLayout(this);
        cardStat.setPadding(60, 50, 60, 50);
        cardStat.setBackground(createRounded(Color.parseColor(CL_CARD), 90));
        
        TextView val = new TextView(this);
        val.setText("Rp 7.593");
        val.setTextSize(35);
        val.setTextColor(Color.parseColor(CL_ACCENT));
        cardStat.addView(val);
        content.addView(cardStat);

        addSpacer(content, 60);
        txtInput = new TextView(this);
        txtInput.setText("TAP UNTUK INPUT...");
        txtInput.setTextSize(18);
        txtInput.setTextColor(Color.GRAY);
        content.addView(txtInput);

        rootWrap.addView(content);
        setupInputLogic(rootWrap);
        setContentView(rootWrap);
    }

    private void setupInputLogic(ViewGroup vg) {
        EditText hiddenEt = new EditText(this);
        hiddenEt.setInputType(InputType.TYPE_CLASS_TEXT);
        hiddenEt.setImeOptions(EditorInfo.IME_ACTION_SEND);
        vg.addView(hiddenEt, new ViewGroup.LayoutParams(1, 1));

        vg.setOnClickListener(v -> {
            hiddenEt.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(hiddenEt, InputMethodManager.SHOW_IMPLICIT);
        });

        hiddenEt.setOnEditorActionListener((v, id, ev) -> {
            txtInput.setText(hiddenEt.getText().toString().toUpperCase());
            hiddenEt.setText("");
            return true;
        });
    }

    private GradientDrawable createRounded(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

    private void addSpacer(LinearLayout l, int h) {
        View s = new View(this);
        l.addView(s, new LinearLayout.LayoutParams(-1, h));
    }
}
