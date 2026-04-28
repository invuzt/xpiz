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
        root.setBackgroundColor(Color.BLACK);
        
        SurfaceView sv = new SurfaceView(this);
        sv.setLayoutParams(new LinearLayout.LayoutParams(-1, 800));
        sv.getHolder().addCallback(this);

        etInput = new EditText(this);
        etInput.setHint("Ketik...");
        etInput.setTextColor(Color.WHITE);

        Button btn = new Button(this);
        btn.setText("RENDER");
        btn.setOnClickListener(v -> {
            if (currentSurface != null) renderToCanvas(currentSurface, etInput.getText().toString());
        });

        root.addView(sv);
        root.addView(etInput);
        root.addView(btn);
        setContentView(root);
    }
    public void surfaceCreated(SurfaceHolder h) { currentSurface = h.getSurface(); }
    public void surfaceChanged(SurfaceHolder h, int f, int w, int h2) {}
    public void surfaceDestroyed(SurfaceHolder h) { currentSurface = null; }
}
