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
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.VERTICAL);
        r.setBackgroundColor(Color.BLACK);
        SurfaceView s = new SurfaceView(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(-1, 800));
        s.getHolder().addCallback(this);
        EditText e = new EditText(this); e.setTextColor(Color.WHITE); e.setHint("Input...");
        Button btn = new Button(this); btn.setText("RENDER");
        btn.setOnClickListener(v -> { if(cur!=null) renderToCanvas(cur, e.getText().toString()); });
        r.addView(s); r.addView(e); r.addView(btn);
        setContentView(r);
    }
    public void surfaceCreated(SurfaceHolder h) { cur = h.getSurface(); }
    public void surfaceChanged(SurfaceHolder h, int f, int w, int h2) {}
    public void surfaceDestroyed(SurfaceHolder h) { cur = null; }
}
