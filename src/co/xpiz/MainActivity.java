package co.xpiz;
import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;

public class MainActivity extends Activity {
    static { System.loadLibrary("xpiz_engine"); }
    private native String prosesDataRust(String data);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(60, 100, 60, 0);

        EditText et = new EditText(this);
        et.setHint("Ketik sesuatu...");
        
        Button btn = new Button(this);
        btn.setText("PROSES DI RUST");

        TextView tv = new TextView(this);
        tv.setTextSize(22);
        tv.setPadding(0, 40, 0, 0);

        btn.setOnClickListener(v -> {
            String hasil = prosesDataRust(et.getText().toString());
            tv.setText(hasil);
        });

        root.addView(et);
        root.addView(btn);
        root.addView(tv);
        setContentView(root);
    }
}
