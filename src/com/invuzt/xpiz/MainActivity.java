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
    private String nextAiPrediction = "";

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
        terminalOutput.setText("--- XPIZ-LANG AI-ENGINE [AUTO-EXPAND ON] ---\n\n");
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(terminalOutput);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        commandInput = new EditText(this);
        commandInput.setTextColor(Color.WHITE);
        commandInput.setHint("type here...");
        commandInput.setHintTextColor(Color.GRAY);
        commandInput.setBackgroundColor(Color.parseColor("#1A1A1A"));
        commandInput.setTypeface(Typeface.MONOSPACE);
        
        // Fitur Auto-Expand: Klik prediksi AI untuk memasukkannya otomatis
        commandInput.setOnLongClickListener(v -> {
            if (!nextAiPrediction.equals("NONE")) {
                commandInput.setText(nextAiPrediction);
                commandInput.setSelection(nextAiPrediction.length());
                Toast.makeText(this, "AI Sugestion Applied", Toast.LENGTH_SHORT).show();
            }
            return true;
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
        
        // Memisahkan Respon dan Prediksi
        String[] parts = raw.split("\\|");
        String response = parts[0];
        nextAiPrediction = parts[1];

        if (response.startsWith("STATUS")) {
            terminalOutput.append("\n[SYS]: Analysis Complete. Hold input to use AI Suggestion.");
        } else {
            terminalOutput.append("\n> " + response);
        }

        if (!nextAiPrediction.equals("NONE")) {
            terminalOutput.append("\n[AI SUGGESTION]: " + nextAiPrediction + " (Long press input to apply)");
        }
    }
}
