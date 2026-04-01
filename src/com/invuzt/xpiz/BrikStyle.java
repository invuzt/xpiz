package com.invuzt.xpiz;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

public class BrikStyle {
    public static final int CL_BG_OUTER = Color.parseColor("#F5F7F9"); 
    public static final int CL_BLACK    = Color.parseColor("#081512"); 
    public static final int CL_ACCENT   = Color.parseColor("#D0C9FF"); 
    public static final int CL_WHITE    = Color.parseColor("#FFFFFF");
    public static final int CL_DARK_CARD = Color.parseColor("#0A1D19");

    // Rounding biasa
    public static GradientDrawable round(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

    // Custom Rounding (Top Left, Top Right, Bottom Right, Bottom Left)
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
}
