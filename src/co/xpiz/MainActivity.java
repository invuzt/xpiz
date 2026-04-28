package co.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.graphics.Color;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    static { System.loadLibrary("xpiz_engine"); }
    private native void renderToCanvas(Surface s, String i);
    private Surface cur;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        
        // Root container dengan padding atas agar tidak nabrak status bar
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#121212"));
        // 100 pixel padding top sebagai pengaman Status Bar
        root.setPadding(40, 120, 40, 40);

        // Frame untuk SurfaceView agar rapi
        SurfaceView s = new SurfaceView(this);
        LinearLayout.LayoutParams sParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 800);
        sParams.bottomMargin = 40;
        s.setLayoutParams(sParams);
        s.getHolder().addCallback(this);

        EditText e = new EditText(this);
        e.setHint("Ketik di sini...");
        e.setTextColor(Color.WHITE);
        e.setHintTextColor(Color.LTGRAY);
        e.setPadding(20, 20, 20, 20);

        Button btn = new Button(this);
        btn.setText("EKSEKUSI RUST");
        
        // Sinkronisasi pemanggilan agar tidak tabrakan memori
        btn.setOnClickListener(v -> {
            if(cur != null && cur.isValid()) {
                String input = e.getText().toString();
                // Gunakan post agar eksekusi tetap aman di antrian main thread namun stabil
                new Thread(() -> {
                    try {
                        renderToCanvas(cur, input);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }).start();
            } else {
                Toast.makeText(this, "Tunggu sebentar, layar belum siap", Toast.LENGTH_SHORT).show();
            }
        });

        root.addView(s);
        root.addView(e);
        root.addView(btn);
        
        setContentView(root);
    }

    @Override
    public void surfaceCreated(SurfaceHolder h) { cur = h.getSurface(); }
    @Override
    public void surfaceChanged(SurfaceHolder h, int f, int w, int h2) {}
    @Override
    public void surfaceDestroyed(SurfaceHolder h) { cur = null; }
}
