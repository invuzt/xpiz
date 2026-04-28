package co.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.graphics.Color;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    static { System.loadLibrary("xpiz_engine"); }
    
    // Fungsi native: Mengirim Surface (Kanvas) dan Teks ke Rust
    private native void renderToCanvas(Surface surface, String input);

    private Surface currentSurface;
    private EditText etInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 80, 40, 0);
        root.setBackgroundColor(Color.DKGRAY);

        // 1. Kanvas (SurfaceView) - Tempat Rust Menggambar
        SurfaceView surfaceView = new SurfaceView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 600);
        lp.bottomMargin = 40;
        surfaceView.setLayoutParams(lp);
        surfaceView.getHolder().addCallback(this);

        // 2. Input Java
        etInput = new EditText(this);
        etInput.setHint("Ketik: Ganjil (Merah) / Genap (Hijau)");
        etInput.setTextColor(Color.WHITE);

        // 3. Tombol Java
        Button btn = new Button(this);
        btn.setText("RENDER DI RUST");
        btn.setOnClickListener(v -> {
            if (currentSurface != null) {
                renderToCanvas(currentSurface, etInput.getText().toString());
            }
        });

        root.addView(surfaceView);
        root.addView(etInput);
        root.addView(btn);
        setContentView(root);
    }

    @Override public void surfaceCreated(SurfaceHolder h) { currentSurface = h.getSurface(); }
    @Override public void surfaceChanged(SurfaceHolder h, int f, int w, int h2) {}
    @Override public void surfaceDestroyed(SurfaceHolder h) { currentSurface = null; }
}
