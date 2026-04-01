package com.invuzt.xpiz;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class BrikStyle {
    // Palet Warna dari Gambar
    public static final int GELAP = Color.parseColor("#081512");
    public static final int AKSEN = Color.parseColor("#D0C9FF"); // Ungu muda
    public static final int PUTIH = Color.WHITE;
    public static final int ABU_TUA = Color.parseColor("#1A1A1A");

    // Fungsi bikin kotak rounded standar
    public static GradientDrawable bulat(int warna, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(warna);
        gd.setCornerRadius(radius);
        return gd;
    }

    // Fungsi bikin kotak dengan garis pinggir (Stroke) - Seperti card di gambar
    pubic static GradientDrawable card(int warnaBg, int warnaGaris, int tebal) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(warnaBg);
        gd.setCornerRadius(100); // Radius besar sesuai gambar
        gd.setStroke(tebal, warnaGaris);
        return gd;
    }
}
