package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.view.inputmethod.EditorInfo;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private TextView displayMode, logOutput;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(40, 40, 40, 40);

        displayMode = new TextView(this);
        displayMode.setTextColor(Color.CYAN);
        displayMode.setTextSize(14);
        displayMode.setText("AI STATUS: READY");
        root.addView(displayMode);

        logOutput = new TextView(this);
        logOutput.setTextColor(Color.GREEN);
        logOutput.setTypeface(Typeface.MONOSPACE);
        logOutput.setTextSize(12);
        
        ScrollView scroll = new ScrollView(this);
        scroll.addView(logOutput);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        input = new EditText(this);
        input.setSingleLine(true);
        input.setTextColor(Color.WHITE);
        input.setHint("Ketik: 'solar 50' atau 'Ajar'...");
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        root.addView(input);

        input.setOnEditorActionListener((v, actionId, event) -> {
            String txt = input.getText().toString();
            if(!txt.isEmpty()){
                String raw = predictBestButton(txt);
                String[] parts = raw.split("\\|");
                displayMode.setText(parts[0]);
                logOutput.append("\n> " + (parts.length > 1 ? parts[1] : raw));
                input.setText("");
            }
            return true;
        });

        setContentView(root);
    }
}
