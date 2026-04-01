package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private TextView terminalOutput;
    private EditText commandInput;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(20, 40, 20, 20);

        terminalOutput = new TextView(this);
        terminalOutput.setTextColor(Color.parseColor("#00FF41"));
        terminalOutput.setTypeface(Typeface.MONOSPACE);
        terminalOutput.setText("--- XPIZ-LANG INTERPRETER v0.1 ---\n> SYSTEM_READY\n\n");
        
        scrollView = new ScrollView(this);
        scrollView.addView(terminalOutput);
        root.addView(scrollView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

        commandInput = new EditText(this);
        commandInput.setTextColor(Color.WHITE);
        commandInput.setHint("xpiz@admin:~$ ");
        commandInput.setHintTextColor(Color.DKGRAY);
        commandInput.setBackgroundColor(Color.TRANSPARENT);
        commandInput.setTypeface(Typeface.MONOSPACE);
        commandInput.setSingleLine(true);
        root.addView(commandInput);

        commandInput.setOnEditorActionListener((v, actionId, event) -> {
            String fullCmd = commandInput.getText().toString().trim();
            if (!fullCmd.isEmpty()) {
                appendToTerminal("\n$ " + fullCmd);
                String res = predictBestButton(fullCmd);
                if (res.startsWith("STATUS")) {
                    handleStatus(res);
                } else {
                    appendToTerminal("\n" + res);
                }
                commandInput.setText("");
            }
            return true;
        });
        setContentView(root);
    }

    private void handleStatus(String raw) {
        String[] p = raw.split("\\|");
        int k = Integer.parseInt(p[1]);
        int s = Integer.parseInt(p[2]);
        int t = Integer.parseInt(p[4]);
        appendToTerminal("\n--- XPIZ-LANG ANALYTICS ---\nKOPI  [" + getBar(k, t) + "] " + k + "\nSABUN [" + getBar(s, t) + "] " + s + "\n---------------------------");
    }

    private String getBar(int v, int t) {
        int len = 10;
        int f = (v * len) / (t > 0 ? t : 1);
        StringBuilder b = new StringBuilder();
        for (int i=0; i<len; i++) b.append(i<f ? "█" : "░");
        return b.toString();
    }

    private void appendToTerminal(String t) {
        terminalOutput.append(t);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}
