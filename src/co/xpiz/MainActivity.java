package co.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.graphics.Color;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    static { System.loadLibrary("xpiz_engine"); }
    private native void renderToCanvas(Surface surface, String input);

    private Surface currentSurface;
    private EditText etInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(50, 80, 50, 50);
        root.setBackgroundColor(Color.parseColor("#121212"));

        // Canvas Rust (SurfaceView)
        SurfaceView sv = new SurfaceView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 700);
        lp.bottomMargin = 50;
        sv.setLayoutParams(lp);
        sv.getHolder().addCallback(this);

        etInput = new EditText(this);
        etInput.setHint("Ketik sesuatu...");
        etInput.setHintTextColor(Color.GRAY);
        etInput.setTextColor(Color.WHITE);

        Button btn = new Button(this);
        btn.setText("RENDER DI RUST");

        btn.setOnClickListener(v -> {
            if (currentSurface != null) {
                renderToCanvas(currentSurface, etInput.getText().toString());
            }
        });

        root.addView(sv);
        root.addView(etInput);
        root.addView(btn);
        setContentView(root);
    }

    @Override public void surfaceCreated(SurfaceHolder h) { currentSurface = h.getSurface(); }
    @Override public void surfaceChanged(SurfaceHolder h, int f, int w, int h2) {}
    @Override public void surfaceDestroyed(SurfaceHolder h) { currentSurface = null; }
}
