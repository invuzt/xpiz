package co.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Button;
import android.graphics.Color;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    static { System.loadLibrary("xpiz_engine"); }
    private native void renderToCanvas(Surface surface, String input);
    private Surface cur;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.VERTICAL);
        r.setBackgroundColor(Color.BLACK);
        
        SurfaceView s = new SurfaceView(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
        s.getHolder().addCallback(this);
        
        EditText e = new EditText(this);
        e.setHint("Ketik di sini...");
        e.setTextColor(Color.WHITE);
        
        Button btn = new Button(this);
        btn.setText("RENDER RUST");
        btn.setOnClickListener(v -> {
            if(cur != null) renderToCanvas(cur, e.getText().toString());
        });

        r.addView(s);
        r.addView(e);
        r.addView(btn);
        setContentView(r);
    }

    public void surfaceCreated(SurfaceHolder h) { cur = h.getSurface(); }
    public void surfaceChanged(SurfaceHolder h, int f, int w, int h2) {}
    public void surfaceDestroyed(SurfaceHolder h) { cur = null; }
}
