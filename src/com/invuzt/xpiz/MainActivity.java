package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("hello");
    }

    private native int checkPasswordStrength(String password);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout sederhana via kode (biar gak ribet urus XML res)
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.setGravity(Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("xpiz Password Checker");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 50);
        layout.addView(title);

        final EditText input = new EditText(this);
        input.setHint("Ketik Password di sini...");
        layout.addView(input);

        final TextView result = new TextView(this);
        result.setText("Skor: 0/5");
        result.setTextSize(18);
        result.setPadding(0, 30, 0, 0);
        layout.addView(result);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int score = checkPasswordStrength(s.toString());
                result.setText("Kekuatan: " + score + "/5");
                
                if (score <= 2) result.setTextColor(Color.RED);
                else if (score <= 4) result.setTextColor(Color.YELLOW);
                else result.setTextColor(Color.GREEN);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        setContentView(layout);
    }
}
