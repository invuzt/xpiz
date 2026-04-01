package com.invuzt.xpiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import java.util.*;
import com.invuzt.logic.CanvasActivity; // Panggil paksa foldernya

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private int totalBelanja = 0;
    private TextView totalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Odfiz POS v2.6");
        toolbar.setBackgroundColor(Color.DKGRAY);
        toolbar.setTitleTextColor(Color.WHITE);
        
        toolbar.getMenu().add(0, 1, 0, "DESIGN LOGIC");
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == 1) {
                Intent it = new Intent(this, CanvasActivity.class);
                startActivity(it);
            }
            return true;
        });
        root.addView(toolbar);

        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextSize(40);
        root.addView(totalView);

        setContentView(root);
    }
}
