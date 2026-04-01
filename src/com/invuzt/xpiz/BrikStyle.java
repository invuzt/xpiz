package com.invuzt.xpiz;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

public class BrikStyle {
    // --- CSS VARIABLES (Warna) ---
    public static final int BG_OUTER = Color.parseColor("#F5F5F5");
    public static final int BG_INNER = Color.parseColor("#000000");
    public static final int ACCENT   = Color.parseColor("#D0C9FF");
    public static final int CARD_W   = Color.parseColor("#FFFFFF");
    public static final int GRAY_TXT = Color.parseColor("#888888");

    // --- CSS UTILS (Fungsi Pembentuk) ---
    public static GradientDrawable round(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

    // Custom Rounding (Top Big, Bottom Small)
    public static GradientDrawable roundCustom(int color, float top, float bottom) {
        float[] radii = {top, top, top, top, bottom, bottom, bottom, bottom};
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadii(radii);
        return gd;
    }

    // Spacer (Margin/Padding)
    public static void space(LinearLayout l, int h) {
        View s = new View(l.getContext());
        l.addView(s, new LinearLayout.LayoutParams(-1, h));
    }

    // --- COMPONENTS (Kaya Class di CSS) ---
    public static TextView createPill(ViewGroup parent, String text, int bg, int txtCol) {
        TextView t = new TextView(parent.getContext());
        t.setText(text);
        t.setPadding(40, 20, 40, 20);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setBackground(round(bg, 100));
        t.setTextColor(txtCol);
        return t;
    }
}
