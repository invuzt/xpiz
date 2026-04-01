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

    private LinearLayout container;
    private TextView totalView, log;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(20, 20, 20, 20);

        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        container = new LinearLayout(this);
        hScroll.addView(container);
        root.addView(hScroll);

        totalView = new TextView(this);
        totalView.setText("TOTAL: 0");
        totalView.setTextColor(Color.YELLOW);
        totalView.setTextSize(35);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        log = new TextView(this);
        log.setTextColor(Color.GREEN);
        log.setTypeface(Typeface.MONOSPACE);
        ScrollView vScroll = new ScrollView(this);
        vScroll.addView(log);
        root.addView(vScroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        EditText input = new EditText(this);
        input.setHint("Ketik Nama : Harga / 'print'");
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setTextColor(Color.WHITE);
        root.addView(input);

        input.setOnEditorActionListener((v, id, event) -> {
            String txt = input.getText().toString();
            if(!txt.isEmpty()){
                String res = predictBestButton(txt);
                if(res.equals("ACTION_PRINT")) {
                    printStruk();
                } else if(res.startsWith("NEW_BTN")) {
                    String[] p = res.split("\\|");
                    makeBtn(p[1], Integer.parseInt(p[2]));
                } else if(res.startsWith("CASH")) {
                    int bayar = (int)Float.parseFloat(res.split("\\|")[1]);
                    log.append("\nTOTAL: " + total + " | BAYAR: " + bayar + "\nKEMBALI: " + (bayar - total));
                }
                input.setText("");
            }
            return true;
        });
        setContentView(root);
    }

    private void makeBtn(String n, int h) {
        Button b = new Button(this);
        b.setText(n + "\n" + h);
        
        // KLIK BIASA: Tambah Harga
        b.setOnClickListener(v -> {
            total += h;
            totalView.setText("TOTAL: " + total);
            log.append("\n+ " + n + " (" + h + ")");
        });

        // TEKAN LAMA (LONG CLICK): Hapus Tombol
        b.setOnLongClickListener(v -> {
            container.removeView(b);
            Toast.makeText(this, "Tombol " + n + " Dihapus", Toast.LENGTH_SHORT).show();
            return true;
        });

        container.addView(b);
    }

    private void printStruk() {
        log.append("\n\n=== STRUK ODFIZ POS ===");
        log.append("\nTOTAL BELANJA: Rp " + total);
        log.append("\n=======================\n");
        total = 0; // Reset total setelah print
        totalView.setText("TOTAL: 0");
    }
}
