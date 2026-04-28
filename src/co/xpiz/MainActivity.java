package co.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.graphics.Color;

public class MainActivity extends Activity {
    static { System.loadLibrary("xpiz_engine"); }
    private native String prosesDataRust(String data);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(60, 120, 60, 0);

        // 1. Output dari Rust (Sekarang di paling atas)
        TextView tvHasil = new TextView(this);
        tvHasil.setText("Hasil akan muncul di sini");
        tvHasil.setTextSize(22);
        tvHasil.setTextColor(Color.BLUE);
        tvHasil.setPadding(0, 0, 0, 50);
        tvHasil.setGravity(Gravity.CENTER);

        // 2. Input Teks
        EditText etInput = new EditText(this);
        etInput.setHint("Ketik sesuatu untuk Rust...");
        
        // 3. Tombol Eksekusi
        Button btnKirim = new Button(this);
        btnKirim.setText("KIRIM KE RUST");

        btnKirim.setOnClickListener(v -> {
            String input = etInput.getText().toString();
            if (!input.isEmpty()) {
                String hasil = prosesDataRust(input);
                tvHasil.setText(hasil);
            }
        });

        // Susun UI sesuai urutan permintaan
        root.addView(tvHasil);  // Hasil di atas
        root.addView(etInput);  // Input di tengah
        root.addView(btnKirim); // Tombol di bawah
        
        setContentView(root);
    }
}
