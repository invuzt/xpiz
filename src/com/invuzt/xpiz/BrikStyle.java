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

    // --- LAYOUT LOCK (Gembok Dimensi) ---
    public static final int RADIUS_BIG    = 110;
    public static final int RADIUS_CARD   = 100;
    public static final int RADIUS_NAV    = 80;
    public static final int PAD_SCREEN    = 60;
    public static final int PAD_HEADER_T  = 130; // Biar mepet status bar

    // --- DRAWABLES ---
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

    public static void space(LinearLayout l, int h) {
        View s = new View(l.getContext());
        l.addView(s, new LinearLayout.LayoutParams(-1, h));
    }

    public static TextView createDescription(ViewGroup parent, String text) {
        TextView tv = new TextView(parent.getContext());
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#B0B0B0"));
        tv.setTextSize(14);
        tv.setLineSpacing(0, 1.4f);
        return tv;
    }
}
