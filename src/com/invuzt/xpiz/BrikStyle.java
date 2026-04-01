package com.invuzt.xpiz;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

public class BrikStyle {
    public static final String BRAND_NAME = "XPIZ®";

    // Variabel warna yang akan berubah-ubah
    public static int CL_BG_OUTER;
    public static int CL_BLACK;
    public static int CL_ACCENT;
    public static int CL_WHITE;
    public static int CL_DARK_CARD;
    public static int CL_TEXT_PRIMARY;

    // Dimensi tetap terkunci
    public static final int RADIUS_CARD = 100;
    public static final int RADIUS_NAV  = 80;
    public static final int PAD_SCREEN  = 60;

    // Fungsi untuk inisialisasi warna berdasarkan tema sistem
    public static void updateTheme(Context ctx) {
        int nightModeFlags = ctx.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // --- DARK MODE (Original XPIZ) ---
            CL_BG_OUTER   = Color.parseColor("#081512");
            CL_BLACK      = Color.parseColor("#081512");
            CL_ACCENT     = Color.parseColor("#D0C9FF");
            CL_WHITE      = Color.parseColor("#FFFFFF");
            CL_DARK_CARD  = Color.parseColor("#0A1D19");
            CL_TEXT_PRIMARY = Color.WHITE;
        } else {
            // --- LIGHT MODE (XPIZ Terang) ---
            CL_BG_OUTER   = Color.parseColor("#F2F2F7"); // Abu-abu sangat muda
            CL_BLACK      = Color.WHITE;                // Background utama jadi putih
            CL_ACCENT     = Color.parseColor("#5856D6"); // Ungu lebih kontras untuk light
            CL_WHITE      = Color.parseColor("#E5E5EA"); // Kartu jadi sedikit abu-abu
            CL_DARK_CARD  = Color.parseColor("#FFFFFF"); // Card statistik jadi putih
            CL_TEXT_PRIMARY = Color.BLACK;              // Teks jadi hitam
        }
    }

    public static GradientDrawable round(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(radius);
        return gd;
    }

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

    public static View createStatCard(ViewGroup parent, String label, String val) {
        LinearLayout card = new LinearLayout(parent.getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(60, 60, 60, 60);
        // Beri stroke tipis kalau di light mode agar terlihat
        GradientDrawable gd = round(CL_DARK_CARD, 80);
        if (CL_TEXT_PRIMARY == Color.BLACK) gd.setStroke(2, Color.LTGRAY);
        card.setBackground(gd);
        
        TextView t1 = new TextView(parent.getContext());
        t1.setText(label); t1.setTextColor(Color.GRAY);
        card.addView(t1);
        
        TextView t2 = new TextView(parent.getContext());
        t2.setText(val); t2.setTextColor(CL_TEXT_PRIMARY); 
        t2.setTextSize(28); t2.setTypeface(Typeface.DEFAULT_BOLD);
        card.addView(t2);
        return card;
    }

    public static void space(LinearLayout l, int h) {
        View s = new View(l.getContext());
        l.addView(s, new LinearLayout.LayoutParams(-1, h));
    }
}
