package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.view.inputmethod.EditorInfo;
import java.util.HashMap;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout container;
    private TextView totalView, log;
    private int total = 0;
    private HashMap<String, Button> menuButtons = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0A0A0A"));
        root.setPadding(20, 20, 20, 20);

        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        container = new LinearLayout(this);
        hScroll.addView(container);
        root.addView(hScroll);

        totalView = new TextView(this);
        totalView.setText("TOTAL: 0");
        totalView.setTextColor(Color.CYAN);
        totalView.setTextSize(40);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        log = new TextView(this);
        log.setTextColor(Color.parseColor("#00FF00"));
        log.setTypeface(Typeface.MONOSPACE);
        ScrollView vScroll = new ScrollView(this);
        vScroll.addView(log);
        root.addView(vScroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        EditText input = new EditText(this);
        input.setHint("Perintah: tambah/hapus/print");
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setTextColor(Color.WHITE);
        root.addView(input);

        input.setOnEditorActionListener((v, id, event) -> {
            String res = predictBestButton(input.getText().toString());
            executeCommand(res);
            input.setText("");
            return true;
        });
        setContentView(root);
    }

    private void executeCommand(String res) {
        String[] p = res.split("\\|");
        if (p[0].equals("CMD_ADD")) {
            createMenuButton(p[1], Integer.parseInt(p[2]));
        } else if (p[0].equals("CMD_DEL")) {
            removeMenuButton(p[1]);
        } else if (p[0].equals("CMD_PRINT")) {
            generateReceipt();
        } else if (p[0].equals("CMD_CASH")) {
            log.append("\n[KAS] Bayar: " + p[1] + " | Sisa: " + (Integer.parseInt(p[1]) - total));
        } else {
            log.append("\n> " + res);
        }
    }

    private void createMenuButton(String nama, int harga) {
        if (menuButtons.containsKey(nama)) return;
        Button b = new Button(this);
        b.setText(nama + "\n" + harga);
        b.setOnClickListener(v -> {
            total += harga;
            totalView.setText("TOTAL: " + total);
            log.append("\n[+] " + nama);
        });
        container.addView(b);
        menuButtons.put(nama, b);
        log.append("\n[OK] Tombol " + nama + " ditambahkan.");
    }

    private void removeMenuButton(String nama) {
        if (menuButtons.containsKey(nama)) {
            container.removeView(menuButtons.get(nama));
            menuButtons.remove(nama);
            log.append("\n[OK] Tombol " + nama + " dihapus.");
        }
    }

    private void generateReceipt() {
        log.append("\n\n--- STRUK DIGITAL ODFIZ ---");
        log.append("\nGrand Total: Rp " + total);
        log.append("\nStok otomatis diperbarui...");
        log.append("\n---------------------------\n");
        total = 0;
        totalView.setText("TOTAL: 0");
    }
}
