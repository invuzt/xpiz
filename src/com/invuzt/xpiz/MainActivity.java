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

    private LinearLayout productContainer, payActionContainer;
    private TextView totalView, log;
    private int totalBelanja = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(20, 20, 20, 20);

        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        productContainer = new LinearLayout(this);
        hScroll.addView(productContainer);
        root.addView(hScroll);

        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextColor(Color.YELLOW);
        totalView.setTextSize(40);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        payActionContainer = new LinearLayout(this);
        payActionContainer.setGravity(Gravity.CENTER);
        root.addView(payActionContainer);

        log = new TextView(this);
        log.setTextColor(Color.GREEN);
        log.setTypeface(Typeface.MONOSPACE);
        ScrollView vScroll = new ScrollView(this);
        vScroll.addView(log);
        root.addView(vScroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        EditText input = new EditText(this);
        input.setHint("Ketik Nama:Harga atau Angka Bayar");
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setTextColor(Color.WHITE);
        root.addView(input);

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String txt = input.getText().toString();
                if(!txt.isEmpty()){
                    handleOutput(predictBestButton(txt));
                    input.setText("");
                }
                return true;
            }
            return false;
        });
        setContentView(root);
    }

    private void handleOutput(String res) {
        String[] p = res.split("\\|");
        if (p[0].equals("ADD")) {
            makeBtn(p[1], Integer.parseInt(p[2]));
        } else if (p[0].equals("PAY_CUSTOM")) {
            finalizePrint((int)Float.parseFloat(p[1]));
        } else if (p[0].equals("SUGGEST")) {
            showSuggestedPay(res);
        }
    }

    private void makeBtn(String n, int h) {
        Button b = new Button(this);
        b.setText(n + "\n" + h);
        b.setOnClickListener(v -> {
            totalBelanja += h;
            totalView.setText("Rp " + totalBelanja);
            handleOutput(predictBestButton("predict|" + totalBelanja));
        });
        b.setOnLongClickListener(v -> {
            if(totalBelanja >= h) totalBelanja -= h;
            totalView.setText("Rp " + totalBelanja);
            handleOutput(predictBestButton("predict|" + totalBelanja));
            return true;
        });
        productContainer.addView(b);
    }

    private void showSuggestedPay(String sug) {
        payActionContainer.removeAllViews();
        String[] p = sug.split("\\|");
        for(int i=1; i<p.length; i++) {
            final int nominal = (int)Float.parseFloat(p[i]);
            Button b = new Button(this);
            b.setText("Rp " + nominal);
            b.setOnClickListener(v -> finalizePrint(nominal));
            payActionContainer.addView(b);
        }
    }

    private void finalizePrint(int bayar) {
        int kembali = bayar - totalBelanja;
        log.setText(""); // New Bill
        log.append("\n=== ODFIZ STRUK ===");
        log.append("\nTOTAL    : " + totalBelanja);
        log.append("\nBAYAR    : " + bayar);
        log.append("\nKEMBALI  : " + kembali);
        log.append("\n====================");
        
        totalBelanja = 0;
        totalView.setText("Rp 0");
        payActionContainer.removeAllViews();
    }
}
