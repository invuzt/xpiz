package com.invuzt.xpiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout productContainer;
    private TextView totalView, aiNotif;
    private int totalBelanja = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20,20,20,20);

        // Notifikasi AI (Barisan atas)
        aiNotif = new TextView(this);
        aiNotif.setText("AI Odfiz: Standby");
        aiNotif.setTextColor(Color.BLUE);
        root.addView(aiNotif);

        HorizontalScrollView h = new HorizontalScrollView(this);
        productContainer = new LinearLayout(this);
        h.addView(productContainer);
        root.addView(h);

        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextSize(40);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        EditText input = new EditText(this);
        input.setHint("Ketik perintah...");
        root.addView(input);

        input.setOnEditorActionListener((v, id, event) -> {
            processAi(input.getText().toString());
            input.setText("");
            return true;
        });
        setContentView(root);
    }

    private void processAi(String cmd) {
        String res = predictBestButton(cmd);
        String[] p = res.split("\\|");
        
        if(p[0].equals("ADD")) addProduct(p[1], Integer.parseInt(p[2]));
        else if(p[0].equals("AI_MSG")) aiNotif.setText("AI: " + p[1]);
        else if(p[0].equals("AI_WARN")) {
            aiNotif.setText("PERINGATAN: " + p[2]);
            aiNotif.setTextColor(Color.RED);
            Toast.makeText(this, "STOK TIPIS!", Toast.LENGTH_LONG).show();
        }
    }

    private void addProduct(String n, int h) {
        Button b = new Button(this);
        b.setText(n);
        b.setOnClickListener(v -> {
            totalBelanja += h;
            totalView.setText("Rp " + totalBelanja);
            // Setiap klik, AI lapor ke Rust untuk potong stok
            predictBestButton("jual " + n);
        });
        productContainer.addView(b);
    }
}
