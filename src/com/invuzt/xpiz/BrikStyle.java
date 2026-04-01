package com.invuzt.xpiz;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class BrikStyle {
    public static final int BG = Color.parseColor("#081512");
    public static final int AKSEN = Color.parseColor("#D0C9FF");
    public static final int PUTIH = Color.WHITE;

    public static GradientDrawable bulat(int warna, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(warna);
        gd.setCornerRadius(radius);
        return gd;
    }
}
