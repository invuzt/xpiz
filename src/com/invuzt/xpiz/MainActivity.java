package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.animation.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(int id);

    private FrameLayout[] buttonContainers = new FrameLayout[3];
    private View[] glowEffects = new View[3];
    private String[] labels = {"KOPI", "SABUN", "STOK"};
    private TextView aiTerminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Background Luar Angkasa (Deep Black)
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#050505"));

        // AI Terminal Header (Matrix Style)
        aiTerminal = new TextView(this);
        aiTerminal.setText("> ODFIZ_AI: INITIALIZING...\n> NEURAL_NETWORK: ACTIVE");
        aiTerminal.setTextColor(Color.parseColor("#00FF41"));
        aiTerminal.setTypeface(Typeface.MONOSPACE);
        aiTerminal.setTextSize(12);
        aiTerminal.setPadding(50, 80, 50, 0);
        root.addView(aiTerminal);

        // Container Tombol di Tengah
        LinearLayout menuContainer = new LinearLayout(this);
        menuContainer.setOrientation(LinearLayout.VERTICAL);
        menuContainer.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.addView(menuContainer, params);

        for (int i = 0; i < 3; i++) {
            final int id = i + 1;
            buttonContainers[i] = new FrameLayout(this);
            
            // Efek Cahaya (Glow)
            glowEffects[i] = new View(this);
            glowEffects[i].setAlpha(0f);
            updateGlow(glowEffects[i], "#00FFFF");
            buttonContainers[i].addView(glowEffects[i], new FrameLayout.LayoutParams(350, 350, Gravity.CENTER));

            // Tombol Glassmorphism Lingkaran
            Button b = new Button(this);
            b.setText(labels[i]);
            b.setTextColor(Color.WHITE);
            b.setTextSize(14);
            b.setTypeface(Typeface.DEFAULT_BOLD);
            updateGlassStyle(b);

            b.setOnClickListener(v -> {
                String result = predictBestButton(id);
                aiTerminal.setText("> USER_INPUT: " + labels[id-1] + "\n" + result.toUpperCase());
                applyAiMagic(result);
            });

            buttonContainers[i].addView(b, new FrameLayout.LayoutParams(250, 250, Gravity.CENTER));
            menuContainer.addView(buttonContainers[i], new LinearLayout.LayoutParams(450, 450));
        }

        setContentView(root);
    }

    private void applyAiMagic(String result) {
        for (int i = 0; i < 3; i++) {
            glowEffects[i].animate().alpha(0f).scaleX(0.5f).scaleY(0.5f).setDuration(300).start();
            buttonContainers[i].animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();
            buttonContainers[i].clearAnimation();
        }

        int target = -1;
        String color = "#00FFFF";
        if (result.contains("KOPI")) { target = 0; color = "#FF00FF"; }
        else if (result.contains("SABUN")) { target = 1; color = "#00FFFF"; }
        else if (result.contains("STOK")) { target = 2; color = "#FFFF00"; }

        if (target != -1) {
            updateGlow(glowEffects[target], color);
            glowEffects[target].animate().alpha(0.7f).scaleX(1.8f).scaleY(1.8f).setDuration(500).start();
            buttonContainers[target].animate().scaleX(1.2f).scaleY(1.2f).setDuration(500).start();
            
            ScaleAnimation pulse = new ScaleAnimation(1.2f, 1.3f, 1.2f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            pulse.setDuration(600);
            pulse.setRepeatMode(Animation.REVERSE);
            pulse.setRepeatCount(Animation.INFINITE);
            buttonContainers[target].startAnimation(pulse);
        }
    }

    private void updateGlow(View v, String color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(Color.parseColor(color));
        gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gd.setGradientRadius(250);
        v.setBackground(gd);
    }

    private void updateGlassStyle(Button b) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(Color.argb(50, 255, 255, 255));
        gd.setStroke(4, Color.argb(150, 255, 255, 255));
        b.setBackground(gd);
    }
}
