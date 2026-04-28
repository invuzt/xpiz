package co.xpiz.engine;
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
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#121212"));
        root.setPadding(40, 150, 40, 40);

        SurfaceView s = new SurfaceView(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(-1, 800));
        s.getHolder().addCallback(this);

        EditText e = new EditText(this);
        e.setHint("Ketik sesuatu..."); e.setTextColor(Color.WHITE);

        Button btn = new Button(this);
        btn.setText("RENDER");
        btn.setOnClickListener(v -> {
            if(cur != null) {
                String input = e.getText().toString();
                new Thread(() -> { try { renderToCanvas(cur, input); } catch(Exception ex){} }).start();
            }
        });

        root.addView(s); root.addView(e); root.addView(btn);
        setContentView(root);
    }
    public void surfaceCreated(SurfaceHolder h) { cur = h.getSurface(); }
    public void surfaceChanged(SurfaceHolder h, int f, int w, int h2) {}
    public void surfaceDestroyed(SurfaceHolder h) { cur = null; }
}
