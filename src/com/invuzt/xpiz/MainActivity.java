package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(int id);

    private TextView terminalOutput;
    private EditText commandInput;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(20, 20, 20, 20);

        terminalOutput = new TextView(this);
        terminalOutput.setTextColor(Color.parseColor("#00FF41"));
        terminalOutput.setTextSize(14);
        terminalOutput.setTypeface(Typeface.MONOSPACE);
        terminalOutput.setText("--- ODFIZ XPIZ OS v1.1 [RUST_INSIGHTS] ---\n> Ready. Type 'xpiz --status' for data.\n\n");
        
        scrollView = new ScrollView(this);
        scrollView.addView(terminalOutput);
        root.addView(scrollView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

        commandInput = new EditText(this);
        commandInput.setTextColor(Color.WHITE);
        commandInput.setHint("xpiz@admin:~$ ");
        commandInput.setHintTextColor(Color.GRAY);
        commandInput.setBackgroundColor(Color.TRANSPARENT);
        commandInput.setTypeface(Typeface.MONOSPACE);
        commandInput.setSingleLine(true);
        root.addView(commandInput);

        commandInput.setOnEditorActionListener((v, actionId, event) -> {
            String cmd = commandInput.getText().toString().trim().toLowerCase();
            if (!cmd.isEmpty()) { handleCommand(cmd); commandInput.setText(""); }
            return true;
        });
        setContentView(root);
    }

    private void handleCommand(String cmd) {
        appendToTerminal("\n$ " + cmd);
        if (cmd.equals("xpiz --status")) {
            String raw = predictBestButton(99); 
            if (raw.startsWith("STATUS")) {
                // Perbaikan di sini: Pakai double backslash untuk escape pipe
                String[] parts = raw.split("\\|");
                try {
                    int k = Integer.parseInt(parts[1]);
                    int s = Integer.parseInt(parts[2]);
                    int t = Integer.parseInt(parts[3]);
                    int total = Integer.parseInt(parts[4]);
                    drawAsciiGraph(k, s, t, total);
                } catch (Exception e) {
                    appendToTerminal("\n[ERR]: Data corruption detected.");
                }
            }
        } else if (cmd.equals("kopi")) { appendToTerminal("\n[AI]: " + predictBestButton(1)); }
        else if (cmd.equals("sabun")) { appendToTerminal("\n[AI]: " + predictBestButton(2)); }
        else if (cmd.equals("stok")) { appendToTerminal("\n[AI]: " + predictBestButton(3)); }
        else if (cmd.equals("clear")) { terminalOutput.setText("--- TERMINAL WIPED ---"); }
        else { appendToTerminal("\n[ERR]: Command not found."); }
    }

    private void drawAsciiGraph(int k, int s, int t, int total) {
        String graph = "\n--- SYSTEM RESOURCE UTILIZATION ---\n";
        // Hindari pembagian dengan nol
        int div = total > 0 ? total : 1;
        graph += "KOPI  [" + getBar(k, div) + "] " + (k*100/div) + "%\n";
        graph += "SABUN [" + getBar(s, div) + "] " + (s*100/div) + "%\n";
        graph += "STOK  [" + getBar(t, div) + "] " + (t*100/div) + "%\n";
        graph += "-----------------------------------\n";
        appendToTerminal(graph);
    }

    private String getBar(int val, int total) {
        int length = 15;
        int filled = (val * length) / total;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i < filled) bar.append("█"); else bar.append("░");
        }
        return bar.toString();
    }

    private void appendToTerminal(String text) {
        terminalOutput.append(text);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}
