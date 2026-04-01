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

    private TextView logView, trendView;
    private EditText inputField;
    private ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0A0A0A"));
        root.setPadding(40, 60, 40, 40);

        // Header
        TextView header = new TextView(this);
        header.setText("ODFIZ PREDICTIVE ENGINE v3.0");
        header.setTextColor(Color.CYAN);
        header.setTextSize(18);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(header);

        // Dashboard Trend
        trendView = new TextView(this);
        trendView.setBackgroundColor(Color.parseColor("#1A1A1A"));
        trendView.setPadding(20, 20, 20, 20);
        trendView.setTextColor(Color.YELLOW);
        trendView.setText("TREND: WAITING FOR DATA...");
        root.addView(trendView);

        // Terminal Log
        logView = new TextView(this);
        logView.setTextColor(Color.parseColor("#00FF41"));
        logView.setTypeface(Typeface.MONOSPACE);
        
        scroll = new ScrollView(this);
        scroll.addView(logView);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        // INPUT FIELD (FIXED ENTER)
        inputField = new EditText(this);
        inputField.setHint("Masukkan data angka...");
        inputField.setTextColor(Color.WHITE);
        inputField.setHintTextColor(Color.GRAY);
        inputField.setSingleLine(true); // Gak bakal bisa kebawah lagi
        inputField.setImeOptions(EditorInfo.IME_ACTION_SEND); // Tombol Enter jadi tombol "Kirim"
        root.addView(inputField);

        inputField.setOnEditorActionListener((v, actionId, event) -> {
            // Cek apakah tombol yang ditekan adalah SEND atau ENTER
            if (actionId == EditorInfo.IME_ACTION_SEND || 
                actionId == EditorInfo.IME_ACTION_DONE || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                
                String in = inputField.getText().toString();
                if(!in.isEmpty()){
                    String res = predictBestButton(in);
                    String[] p = res.split("\\|");
                    trendView.setText(p[0]);
                    logView.append("\n[IN]: " + in + " -> " + (p.length > 1 ? p[1] : "Calculating..."));
                    inputField.setText(""); // Langsung kosongin buat input selanjutnya
                    scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
                }
                return true; // Bilang ke sistem: "Enter sudah saya tangani, jangan bikin baris baru!"
            }
            return false;
        });

        setContentView(root);
    }
}
