package com.invuzt.xpiz;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import java.io.File;
import java.util.*;

public class MainActivity extends Activity {
    static { try { System.loadLibrary("hello"); } catch (UnsatisfiedLinkError e) {} }

    private native String saveMarkdownNative(String path, String content);
    private native String listVaultFiles(String dirPath);
    private native String readMarkdownNative(String path);
    private native String renderMarkdownNative(String content);
    private native String getGraphDataNative(String dirPath);

    private EditText etTitle, etBody;
    private TextView tvPreview;
    private ArrayList<String> fileList;
    private ArrayAdapter<String> adapter;

    private File getVaultFolder() {
        File docs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File vault = new File(docs, "OdfizVault");
        if (!vault.exists()) vault.mkdirs();
        return vault;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 40);
        root.setBackgroundColor(Color.WHITE);

        TextView tvTime = new TextView(this);
        tvTime.setText(new java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        tvTime.setTextSize(48);
        tvTime.setTextColor(Color.BLACK);
        tvTime.setGravity(Gravity.CENTER);
        root.addView(tvTime);

        etTitle = new EditText(this);
        etTitle.setHint("Judul...");
        etTitle.setTextColor(Color.BLACK);
        root.addView(etTitle);

        etBody = new EditText(this);
        etBody.setHint("Tulis ide...");
        etBody.setLines(4);
        etBody.setTextColor(Color.BLACK);
        root.addView(etBody);

        tvPreview = new TextView(this);
        tvPreview.setTextColor(Color.BLACK);
        tvPreview.setLinkTextColor(Color.BLUE);
        tvPreview.setVisibility(View.GONE);
        tvPreview.setMovementMethod(LinkMovementMethod.getInstance());
        root.addView(tvPreview);

        // Baris Tombol Menu
        HorizontalScrollView hsv = new HorizontalScrollView(this);
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setGravity(Gravity.CENTER);
        
        Button btnSave = new Button(this); btnSave.setText("Simpan");
        Button btnGraph = new Button(this); btnGraph.setText("Graph");
        Button btnApps = new Button(this); btnApps.setText("Apps");
        Button btnSetHome = new Button(this); btnSetHome.setText("Set Home"); // Tombol baru

        btnRow.addView(btnSave); btnRow.addView(btnGraph); 
        btnRow.addView(btnApps); btnRow.addView(btnSetHome);
        hsv.addView(btnRow);
        root.addView(hsv);

        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList) {
            @Override
            public View getView(int pos, View convert, ViewGroup parent) {
                View v = super.getView(pos, convert, parent);
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                return v;
            }
        };
        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        root.addView(lv);

        btnSave.setOnClickListener(v -> {
            String t = etTitle.getText().toString();
            if(t.isEmpty()) t = "Ide_" + System.currentTimeMillis();
            if(!t.endsWith(".md")) t += ".md";
            saveMarkdownNative(new File(getVaultFolder(), t).getAbsolutePath(), etBody.getText().toString());
            refreshVault();
            Toast.makeText(this, "Tersimpan", Toast.LENGTH_SHORT).show();
        });

        btnApps.setOnClickListener(v -> showAppDrawer());
        btnGraph.setOnClickListener(v -> showGraph());
        btnSetHome.setOnClickListener(v -> openHomeSettings());
        lv.setOnItemClickListener((p, v, pos, id) -> openFile(fileList.get(pos)));

        refreshVault();
        setContentView(root);
    }

    private void openHomeSettings() {
        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
        startActivity(intent);
    }

    private void refreshVault() {
        String res = listVaultFiles(getVaultFolder().getAbsolutePath());
        fileList.clear();
        if(!res.equals("Kosong")) for(String f : res.split("\\|")) fileList.add(f);
        adapter.notifyDataSetChanged();
    }

    private void openFile(String name) {
        File f = new File(getVaultFolder(), name);
        etTitle.setText(name);
        etBody.setText(readMarkdownNative(f.getAbsolutePath()));
        tvPreview.setVisibility(View.GONE);
        etBody.setVisibility(View.VISIBLE);
    }

    private void showAppDrawer() {
        final Dialog d = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        LinearLayout drawer = new LinearLayout(this);
        drawer.setOrientation(LinearLayout.VERTICAL);
        drawer.setBackgroundColor(Color.WHITE);
        drawer.setPadding(20, 20, 20, 20);

        ListView lvApps = new ListView(this);
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> pkgList = getPackageManager().queryIntentActivities(mainIntent, 0);
        
        List<String> appNames = new ArrayList<>();
        for (ResolveInfo ri : pkgList) appNames.add(ri.loadLabel(getPackageManager()).toString());

        lvApps.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appNames));
        lvApps.setOnItemClickListener((p, v, pos, id) -> {
            ResolveInfo info = pkgList.get(pos);
            Intent i = getPackageManager().getLaunchIntentForPackage(info.activityInfo.packageName);
            if(i != null) startActivity(i);
            d.dismiss();
        });

        drawer.addView(lvApps);
        d.setContentView(drawer);
        d.show();
    }

    private void showGraph() {
        // [Kode ZoomGraphView tetap sama seperti sebelumnya]
        String data = getGraphDataNative(getVaultFolder().getAbsolutePath());
        final Dialog d = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        
        class ZoomGraphView extends View {
            private Matrix matrix = new Matrix();
            private ScaleGestureDetector scaleDetector;
            private float lastX, lastY;
            private Map<String, PointF> nodes = new HashMap<>();
            private String graphData;

            public ZoomGraphView(android.content.Context ctx, String data) {
                super(ctx);
                this.graphData = data;
                scaleDetector = new ScaleGestureDetector(ctx, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override public boolean onScale(ScaleGestureDetector detector) {
                        matrix.postScale(detector.getScaleFactor(), detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
                        invalidate();
                        return true;
                    }
                });
                if(!data.equals("Kosong")) {
                    Random r = new Random();
                    for(String pair : data.split("\\|")) {
                        for(String n : pair.split(":")) {
                            if(!nodes.containsKey(n)) nodes.put(n, new PointF(r.nextInt(800)+200, r.nextInt(1000)+200));
                        }
                    }
                }
            }

            @Override protected void onDraw(Canvas c) {
                c.save();
                c.concat(matrix);
                if(graphData.equals("Kosong")) return;
                Paint pLine = new Paint(); pLine.setColor(Color.LTGRAY); pLine.setStrokeWidth(3);
                Paint pNode = new Paint(); pNode.setColor(Color.parseColor("#2196F3")); pNode.setAntiAlias(true);
                Paint pText = new Paint(); pText.setColor(Color.BLACK); pText.setTextSize(30);
                String[] pairs = graphData.split("\\|");
                for(String pair : pairs) {
                    String[] pts = pair.split(":");
                    if(pts.length < 2) continue;
                    PointF p1 = nodes.get(pts[0]), p2 = nodes.get(pts[1]);
                    if(p1 != null && p2 != null) c.drawLine(p1.x, p1.y, p2.x, p2.y, pLine);
                }
                for(String n : nodes.keySet()) {
                    PointF pt = nodes.get(n);
                    c.drawCircle(pt.x, pt.y, 15, pNode);
                    c.drawText(n, pt.x+20, pt.y+10, pText);
                }
                c.restore();
            }

            @Override public boolean onTouchEvent(MotionEvent ev) {
                scaleDetector.onTouchEvent(ev);
                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: lastX = ev.getX(); lastY = ev.getY(); break;
                    case MotionEvent.ACTION_MOVE:
                        if(!scaleDetector.isInProgress()) {
                            matrix.postTranslate(ev.getX() - lastX, ev.getY() - lastY);
                            lastX = ev.getX(); lastY = ev.getY();
                            invalidate();
                        }
                        break;
                }
                return true;
            }
        }

        FrameLayout layout = new FrameLayout(this);
        ZoomGraphView gv = new ZoomGraphView(this, data);
        gv.setBackgroundColor(Color.WHITE);
        layout.addView(gv);
        Button btnX = new Button(this); btnX.setText("X");
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(150, 150);
        lp.gravity = Gravity.TOP | Gravity.RIGHT;
        btnX.setLayoutParams(lp);
        btnX.setOnClickListener(v -> d.dismiss());
        layout.addView(btnX);
        d.setContentView(layout);
        d.show();
    }
}
