package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import static com.invuzt.xpiz.BrikStyle.*; // Import "CSS" kita

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getHelloFromRust();

    private LinearLayout mainContentArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Root Setup pakai variabel dari BrikStyle
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(BG_OUTER);
        root.setPadding(30, 30, 30, 30);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setBackground(round(BG_INNER, 120));
        root.addView(inner, new RelativeLayout.LayoutParams(-1, -1));

        // Content Area
        mainContentArea = new LinearLayout(this);
        mainContentArea.setOrientation(LinearLayout.VERTICAL);
        mainContentArea.setPadding(60, 40, 60, 300);
        
        ScrollView sv = new ScrollView(this);
        sv.addView(mainContentArea);
        inner.addView(sv);

        showTrainingPage();
        setContentView(root);
    }

    private void showTrainingPage() {
        mainContentArea.removeAllViews();
        
        // Contoh panggil komponen "CSS"
        TextView card = new TextView(this);
        card.setText("Rhythm Match");
        card.setBackground(round(CARD_W, 100));
        card.setPadding(60, 50, 60, 50);
        card.setTextColor(Color.BLACK);
        
        mainContentArea.addView(card);
        space(mainContentArea, 40); // Pakai spacer dari BrikStyle
        
        // Pakai data dari Rust
        TextView rustMsg = new TextView(this);
        rustMsg.setText(getHelloFromRust());
        rustMsg.setTextColor(ACCENT);
        mainContentArea.addView(rustMsg);
    }
}
