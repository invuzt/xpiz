package com.invuzt.xpiz;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

public class BrikStyle {
    // Warna sesuai Gambar Brik
    public static final int CL_BG_OUTER = Color.parseColor("#F5F5F5"); 
    public static final int CL_BG_INNER = Color.parseColor("#000000"); 
    public static final int CL_ACCENT   = Color.parseColor("#D0C9FF"); 
    public static final int CL_CARD_W   = Color.parseColor("#FFFFFF"); 

    // Helper Rounding
    public static GradientDrawable drawRound(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

    // Custom Rounding (Top: 100px, Bottom: 30px) - Buat Sequence Rush
    public static GradientDrawable drawHalfRound(int color, boolean topBig) {
        float rB = 100f; float rS = 30f;
        float[] radii = topBig ? 
            new float[]{rB, rB, rB, rB, rS, rS, rS, rS} : 
            new float[]{rS, rS, rS, rS, rB, rB, rB, rB};
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadii(radii);
        return gd;
    }

    public static void addSpace(LinearLayout l, int h) {
        View s = new View(l.getContext());
        l.addView(s, new LinearLayout.LayoutParams(-1, h));
    }
}
