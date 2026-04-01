package com.invuzt.xpiz;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import static com.invuzt.xpiz.BrikStyle.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String getSystemConfig(String k);
    private native String getContentFromRust(int id);
    // Sekarang menerima dua string: Tag dan Value
    private native String handleTouch(String tag, String val);

    private LinearLayout contentArea, navContainer;
    private TextView tvLevel, tvLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(512, 512);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor(getSystemConfig("COLOR_GELAP")));

        // Header
        RelativeLayout header = new RelativeLayout(this);
        header.setId(View.generateViewId());
        header.setPadding(60, 150, 60, 40);

        tvLogo = new TextView(this);
        tvLogo.setText(getSystemConfig("LOGO"));
        tvLogo.setTextSize(28);
        tvLogo.setTypeface(null, Typeface.BOLD);
        tvLogo.setTextColor(Color.WHITE);
        tvLogo.setOnClickListener(v -> {
            if(handleTouch("HEADER_CLICK", "").startsWith("GOTO:")) buka(99);
        });
        header.addView(tvLogo);

        tvLevel = new TextView(this);
        tvLevel.setText(getSystemConfig("NOTIF"));
        tvLevel.setPadding(35, 12, 35, 12);
        tvLevel.setBackground(bulat(Color.parseColor("#D0C9FF"), 50));
        tvLevel.setTextColor(Color.BLACK);
        tvLevel.setOnClickListener(v -> { handleTouch("NOTIF_CLICK", ""); buka(1); });
        
        RelativeLayout.LayoutParams lpL = new RelativeLayout.LayoutParams(-2,-2);
        lpL.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        header.addView(tvLevel, lpL);
        root.addView(header);

        contentArea = new LinearLayout(this);
        contentArea.setOrientation(1);
        contentArea.setPadding(40, 20, 40, 400);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(contentArea);
        RelativeLayout.LayoutParams lpS = new RelativeLayout.LayoutParams(-1,-1);
        lpS.addRule(RelativeLayout.BELOW, header.getId());
        root.addView(scroll, lpS);

        setContentView(root);
        buka(1);
    }

    void buka(int id) {
        contentArea.removeAllViews();
        String data = getContentFromRust(id);
        tvLevel.setText(getSystemConfig("NOTIF"));

        for (String line : data.split("\n")) {
            String[] part = line.split("\\|");
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(1);
            item.setBackground(card(Color.parseColor("#1A1A1A"), 0, 0));
            item.setPadding(50,50,50,50);

            TextView label = new TextView(this);
            label.setText(part[0]);
            label.setTextColor(Color.WHITE);
            item.addView(label);

            final EditText et = new EditText(this);
            if(part[1].equals("INPUT")) {
                et.setHint("Ketik...");
                et.setTextColor(Color.CYAN);
                item.addView(et);
            }

            item.setOnClickListener(v -> {
                // Ambil teks dari EditText jika ada
                String inputVal = et.getText().toString();
                String res = handleTouch(part[0], inputVal);
                
                if(res.startsWith("GOTO:")) {
                    buka(Integer.parseInt(res.split(":")[1]));
                } else if(res.equals("REFRESH")) {
                    buka(id);
                }
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 30);
            contentArea.addView(item, lp);
        }
    }
}
