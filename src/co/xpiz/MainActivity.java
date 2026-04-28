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
        root.setPadding(60, 100, 60, 0);
        root.setBackgroundColor(Color.WHITE);

        // Hasil di atas (Teks normal)
        TextView tvHasil = new TextView(this);
        tvHasil.setText("Menunggu input...");
        tvHasil.setTextSize(20);
        tvHasil.setTextColor(Color.parseColor("#2E7D32")); // Warna hijau gelap biar bagus
        tvHasil.setPadding(0, 0, 0, 60);
        tvHasil.setGravity(Gravity.CENTER);

        EditText etInput = new EditText(this);
        etInput.setHint("Ketik di sini...");
        
        Button btnKirim = new Button(this);
        btnKirim.setText("Kirim ke Rust");

        btnKirim.setOnClickListener(v -> {
            String input = etInput.getText().toString();
            if (!input.trim().isEmpty()) {
                tvHasil.setText(prosesDataRust(input));
            }
        });

        root.addView(tvHasil);
        root.addView(etInput);
        root.addView(btnKirim);
        
        setContentView(root);
    }
}
