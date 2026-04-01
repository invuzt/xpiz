package com.invuzt.xpiz;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

public class BrikStyle {
    // --- BRANDING ---
    public static final String BRAND_NAME = "XPIZ®";

    // --- COLORS ---
    public static final int CL_BG_OUTER  = Color.parseColor("#081512"); 
    public static final int CL_BLACK     = Color.parseColor("#081512"); 
    public static final int CL_ACCENT    = Color.parseColor("#D0C9FF"); 
    public static final int CL_WHITE     = Color.parseColor("#FFFFFF");
    public static final int CL_DARK_CARD = Color.parseColor("#0A1D19");

    // --- DIMENSIONS ---
    public static final int RADIUS_CARD = 100;
    public static final int RADIUS_NAV  = 80;
    public static final int PAD_SCREEN  = 60;

    // --- CORE DRAWABLES ---
    public static GradientDrawable round(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

    public static GradientDrawable roundCorners(int color, float tl, float tr, float br, float bl) {
        float[] radii = {tl, tl, tr, tr, br, br, bl, bl};
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadii(radii);
        return gd;
    }

    // --- COMPONENT FACTORY (Gembok Layout di Sini) ---

    // 1. Kartu Putih (Training Card)
    public static View createWhiteCard(ViewGroup parent, String text) {
        RelativeLayout card = new RelativeLayout(parent.getContext());
        card.setPadding(60, 60, 60, 60);
        card.setBackground(round(CL_WHITE, RADIUS_CARD));
        
        TextView t = new TextView(parent.getContext());
        t.setText(text); t.setTextColor(Color.BLACK); t.setTextSize(18);
        card.addView(t);

        TextView arrow = new TextView(parent.getContext());
        arrow.setText(">"); arrow.setTextColor(Color.GRAY);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        card.addView(arrow, lp);
        
        return card;
    }

    // 2. Kartu Statistik (Progress Card)
    public static View createStatCard(ViewGroup parent, String label, String val) {
        LinearLayout card = new LinearLayout(parent.getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(60, 60, 60, 60);
        card.setBackground(round(CL_DARK_CARD, 80));
        
        TextView t1 = new TextView(parent.getContext());
        t1.setText(label); t1.setTextColor(Color.GRAY);
        card.addView(t1);
        
        TextView t2 = new TextView(parent.getContext());
        t2.setText(val); t2.setTextColor(Color.WHITE); 
        t2.setTextSize(28); t2.setTypeface(Typeface.DEFAULT_BOLD);
        card.addView(t2);
        
        return card;
    }

    public static void space(LinearLayout l, int h) {
        View s = new View(l.getContext());
        l.addView(s, new LinearLayout.LayoutParams(-1, h));
    }
}
