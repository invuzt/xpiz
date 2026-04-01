package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.text.method.ScrollingMovementMethod;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(int id);

    private TextView terminalOutput;
    private EditText commandInput;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Root Layout (Deep Black)
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(20, 20, 20, 20);

        // Terminal Output (Green Matrix Text)
        terminalOutput = new TextView(this);
        terminalOutput.setTextColor(Color.parseColor("#00FF41"));
        terminalOutput.setTextSize(14);
        terminalOutput.setTypeface(Typeface.MONOSPACE);
        terminalOutput.setText("--- ODFIZ XPIZ OS v1.0 [OFFLINE_AI] ---\n> Type 'help' for commands\n\n");
        
        scrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
        scrollView.addView(terminalOutput);
        root.addView(scrollView, scrollParams);

        // Input Area
        LinearLayout inputArea = new LinearLayout(this);
        inputArea.setOrientation(LinearLayout.HORIZONTAL);
        inputArea.setPadding(0, 20, 0, 0);

        TextView prompt = new TextView(this);
        prompt.setText("xpiz@admin:~$ ");
        prompt.setTextColor(Color.WHITE);
        prompt.setTypeface(Typeface.MONOSPACE);
        inputArea.addView(prompt);

        commandInput = new EditText(this);
        commandInput.setBackgroundColor(Color.TRANSPARENT);
        commandInput.setTextColor(Color.WHITE);
        commandInput.setTypeface(Typeface.MONOSPACE);
        commandInput.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_SEND);
        commandInput.setSingleLine(true);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputArea.addView(commandInput, inputParams);

        root.addView(inputArea);

        // Event Listener (Enter Key)
        commandInput.setOnEditorActionListener((v, actionId, event) -> {
            String cmd = commandInput.getText().toString().trim().toLowerCase();
            if (!cmd.isEmpty()) {
                handleCommand(cmd);
                commandInput.setText("");
            }
            return true;
        });

        setContentView(root);
    }

    private void handleCommand(String cmd) {
        appendToTerminal("\n\n[USER]: " + cmd);
        
        String response;
        if (cmd.equals("help")) {
            response = "> Available commands:\n  - kopi  : Log coffee transaction\n  - sabun : Log soap transaction\n  - stok  : Check predictive stock\n  - clear : Wipe terminal";
        } else if (cmd.equals("clear")) {
            terminalOutput.setText("--- TERMINAL WIPED ---");
            return;
        } else if (cmd.contains("kopi")) {
            response = predictBestButton(1);
        } else if (cmd.contains("sabun")) {
            response = predictBestButton(2);
        } else if (cmd.contains("stok")) {
            response = predictBestButton(3);
        } else {
            response = "> Error: Unknown command '" + cmd + "'";
        }

        appendToTerminal("\n[ODFIZ_AI]: " + response);
    }

    private void appendToTerminal(String text) {
        terminalOutput.append(text);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}
