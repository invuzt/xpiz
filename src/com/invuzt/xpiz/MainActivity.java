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

    // Fungsi native sekarang mengembalikan String
    private native String getPasswordAdvice(String password);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 60, 60, 60);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("xpiz Password Auditor");
        title.setTextSize(22);
        title.setPadding(0, 0, 0, 40);
        layout.addView(title);

        final EditText input = new EditText(this);
        input.setHint("Ketik password untuk dicek...");
        layout.addView(input);

        final TextView adviceView = new TextView(this);
        adviceView.setTextSize(14);
        adviceView.setPadding(0, 30, 0, 0);
        adviceView.setGravity(Gravity.CENTER);
        layout.addView(adviceView);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String advice = getPasswordAdvice(s.toString());
                adviceView.setText(advice);
                
                if (advice.contains("Lemah")) {
                    adviceView.setTextColor(Color.RED);
                } else if (advice.contains("Sangat Kuat")) {
                    adviceView.setTextColor(Color.GREEN);
                } else {
                    adviceView.setTextColor(Color.YELLOW);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        setContentView(layout);
    }
}
