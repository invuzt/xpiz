package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.graphics.Color;
import android.graphics.Typeface;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("hello");
    }

    private native String getPasswordAdvice(String password);
    private native boolean savePasswordNative(String password);

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
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        // Deskripsi Privasi
        TextView privacyInfo = new TextView(this);
        privacyInfo.setText("🔒 Keamanan Lokal Terjamin\nPassword diproses langsung oleh modul biner Rust di dalam HP Anda. Tidak ada data yang dikirim ke internet atau disimpan oleh developer.");
        privacyInfo.setTextSize(12);
        privacyInfo.setGravity(Gravity.CENTER);
        privacyInfo.setPadding(0, 20, 0, 40);
        layout.addView(privacyInfo);

        final EditText input = new EditText(this);
        input.setHint("Ketik password...");
        layout.addView(input);

        final TextView adviceView = new TextView(this);
        adviceView.setTextSize(14);
        adviceView.setPadding(0, 30, 0, 30);
        layout.addView(adviceView);

        final Button btnSave = new Button(this);
        btnSave.setText("Simpan Secara Aman");
        btnSave.setEnabled(false);
        layout.addView(btnSave);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String advice = getPasswordAdvice(s.toString());
                adviceView.setText(advice);
                
                Log.d("xpiz_Rust", "Engine bekerja: " + advice);

                if (advice.contains("Sangat Kuat") || advice.contains("Cukup")) {
                    adviceView.setTextColor(Color.GREEN);
                    btnSave.setEnabled(true);
                } else {
                    adviceView.setTextColor(Color.RED);
                    btnSave.setEnabled(false);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = savePasswordNative(input.getText().toString());
                if (success) {
                    Toast.makeText(MainActivity.this, "Tersimpan aman di Vault Rust!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setContentView(layout);
    }
}
