package co.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.Color;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("xpiz_engine");
    }

    private native String getMsg(String name);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setTextSize(26);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLUE);
        
        // Panggil Rust
        tv.setText(getMsg("Sistem"));
        
        setContentView(tv);
    }
}
