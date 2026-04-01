package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.text.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private TextView terminalOutput;
    private EditText commandInput;
    private ScrollView scroll;
    private String nextAiPrediction = "NONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(30, 50, 30, 30);

        terminalOutput = new TextView(this);
        terminalOutput.setTextColor(Color.parseColor("#00FF41"));
        terminalOutput.setTypeface(Typeface.MONOSPACE);
        terminalOutput.setText("--- XPIZ-LANG AI-ENGINE [STABLE_v1.2] ---\n\n");
        
        scroll = new ScrollView(this);
        scroll.addView(terminalOutput);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        commandInput = new EditText(this);
        commandInput.setTextColor(Color.WHITE);
        commandInput.setHint("xpiz@admin:~$ ");
        commandInput.setHintTextColor(Color.DKGRAY);
        commandInput.setBackgroundColor(Color.parseColor("#121212"));
        commandInput.setTypeface(Typeface.MONOSPACE);
        commandInput.setSingleLine(true);
        
        // FIX: Agar sugesti tidak hilang saat diterapkan
        commandInput.setOnLongClickListener(v -> {
            if (nextAiPrediction != null && !nextAiPrediction.equals("NONE")) {
                commandInput.setText(nextAiPrediction);
                // Paksa kursor ke paling kanan
                commandInput.setSelection(commandInput.getText().length());
                commandInput.requestFocus();
                
                // Beri getaran kecil atau toast sebagai tanda sukses
                Toast.makeText(this, "🤖 AI Auto-Filled: " + nextAiPrediction, Toast.LENGTH_SHORT).show();
                return true; // Menandakan event sudah ditangani
            }
            return false;
        });

        commandInput.setOnEditorActionListener((v, actionId, event) -> {
            String fullCmd = commandInput.getText().toString().trim();
            if (!fullCmd.isEmpty()) {
                execute(fullCmd);
                commandInput.setText("");
            }
            return true;
        });

        root.addView(commandInput);
        setContentView(root);
    }

    private void execute(String cmd) {
        terminalOutput.append("\n$ " + cmd);
        String raw = predictBestButton(cmd);
        
        // Split data dari Rust (Response|Prediction)
        String[] parts = raw.split("\\|");
        if (parts.length >= 2) {
            String response = parts[0];
            nextAiPrediction = parts[1];

            if (response.startsWith("STATUS")) {
                handleStatus(response);
            } else {
                terminalOutput.append("\n> " + response);
            }

            if (!nextAiPrediction.equals("NONE")) {
                terminalOutput.append("\n[AI SUGGEST]: " + nextAiPrediction + " (Hold to use)");
            }
        }
        
        // Scroll otomatis ke bawah
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }

    private void handleStatus(String raw) {
        String[] p = raw.split("\\|");
        // Logika bar chart ASCII seperti sebelumnya
        terminalOutput.append("\n[SYS]: Analytics Ready.");
    }
}
