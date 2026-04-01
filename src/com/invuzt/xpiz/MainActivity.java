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
    private String ghostText = "";

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
        terminalOutput.setText("--- ODFIZ XPIZ AI [GHOST_INPUT_ENABLED] ---\n\n");
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(terminalOutput);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        // Area Input dengan tombol TAB
        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);
        inputLayout.setBackgroundColor(Color.parseColor("#121212"));

        commandInput = new EditText(this);
        commandInput.setTextColor(Color.WHITE);
        commandInput.setHint("xpiz@admin:~$ ");
        commandInput.setHintTextColor(Color.DKGRAY);
        commandInput.setBackgroundColor(Color.TRANSPARENT);
        commandInput.setTypeface(Typeface.MONOSPACE);
        commandInput.setSingleLine(true);
        
        // Tombol TAB Modern
        Button tabBtn = new Button(this);
        tabBtn.setText("TAB");
        tabBtn.setTextColor(Color.CYAN);
        tabBtn.setBackgroundColor(Color.parseColor("#222222"));
        tabBtn.setOnClickListener(v -> applyGhostText());

        inputLayout.addView(commandInput, new LinearLayout.LayoutParams(0, -2, 1.0f));
        inputLayout.addView(tabBtn, new LinearLayout.LayoutParams(150, -2));
        root.addView(inputLayout);

        // LOGIKA GHOST TEXT: Muncul saat mengetik
        commandInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                if (!input.isEmpty()) {
                    // Tanya Rust: "Kalau saya ngetik ini, prediksinya apa?"
                    String raw = predictBestButton(input);
                    String[] parts = raw.split("\\|");
                    if (parts.length >= 2 && !parts[1].equals("NONE")) {
                        ghostText = parts[1];
                        // Menampilkan petunjuk abu-abu di hint
                        commandInput.setHint(input + " (" + ghostText + ")");
                    } else {
                        ghostText = "";
                        commandInput.setHint("xpiz@admin:~$ ");
                    }
                }
            }
            public void afterTextChanged(Editable s) {}
        });

        commandInput.setOnEditorActionListener((v, actionId, event) -> {
            String fullCmd = commandInput.getText().toString().trim();
            if (!fullCmd.isEmpty()) {
                execute(fullCmd);
                commandInput.setText("");
                commandInput.setHint("xpiz@admin:~$ ");
            }
            return true;
        });

        setContentView(root);
    }

    private void applyGhostText() {
        if (!ghostText.isEmpty()) {
            commandInput.setText(ghostText);
            commandInput.setSelection(ghostText.length());
        }
    }

    private void execute(String cmd) {
        terminalOutput.append("\n$ " + cmd);
        String raw = predictBestButton(cmd);
        String[] parts = raw.split("\\|");
        terminalOutput.append("\n> " + parts[0]);
    }
}
