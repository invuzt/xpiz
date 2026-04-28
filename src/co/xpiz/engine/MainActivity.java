package co.xpiz.engine;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import android.graphics.Color;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    static { System.loadLibrary("xpiz_engine"); }
    private native void renderToCanvas(Surface s, String i);
    private Surface cur;
    private boolean isRunning = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private String particleCount = "100"; // Default

    // Loop Animasi (FPS control)
    private final Runnable animationLoop = new Runnable() {
        @Override
        public void run() {
            if (isRunning && cur != null && cur.isValid()) {
                try {
                    // Panggil Rust Engine setiap frame
                    renderToCanvas(cur, particleCount);
                } catch (Exception e) {}
                // Jeda 16ms untuk mendapatkan ~60 FPS
                handler.postDelayed(this, 16); 
            }
        }
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#121212"));
        // Status bar safety
        root.setPadding(40, 220, 40, 40); 

        SurfaceView s = new SurfaceView(this);
        // Buat view agak besar agar puas lihatnya
        s.setLayoutParams(new LinearLayout.LayoutParams(-1, 1000)); 
        s.getHolder().addCallback(this);

        EditText e = new EditText(this);
        e.setHint("Jumlah partikel (contoh: 1000)");
        e.setTextColor(Color.WHITE);
        e.setHintTextColor(Color.GRAY);
        e.setInputType(android.text.InputType.TYPE_CLASS_NUMBER); // Paksa input angka

        Button btn = new Button(this);
        btn.setText("TERAPKAN & START");
        
        btn.setOnClickListener(v -> {
            particleCount = e.getText().toString();
            if(particleCount.isEmpty()) particleCount = "100";
            if (!isRunning) {
                isRunning = true;
                handler.post(animationLoop); // Start loop
                btn.setText("UPDATE JUMLAH");
            }
        });

        root.addView(s); root.addView(e); root.addView(btn);
        setContentView(root);
    }

    @Override
    public void surfaceCreated(SurfaceHolder h) { 
        cur = h.getSurface();
    }
    @Override
    public void surfaceChanged(SurfaceHolder h, int f, int w, int h2) {}
    @Override
    public void surfaceDestroyed(SurfaceHolder h) { 
        cur = null; 
        isRunning = false; // Stop loop agar tidak FC
    }
}
