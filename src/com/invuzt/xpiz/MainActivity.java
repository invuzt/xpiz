package com.invuzt.xpiz;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
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
        root.setPadding(30, 30, 30, 30);
        root.setBackgroundColor(Color.WHITE);

        etTitle = new EditText(this);
        etTitle.setHint("Judul (tanpa .md)");
        etTitle.setTextColor(Color.BLACK);
        root.addView(etTitle);

        etBody = new EditText(this);
        etBody.setHint("Tulis isi...");
        etBody.setLines(8);
        etBody.setTextColor(Color.BLACK);
        etBody.setGravity(Gravity.TOP);
        root.addView(etBody);

        tvPreview = new TextView(this);
        tvPreview.setTextColor(Color.BLACK);
        tvPreview.setLinkTextColor(Color.BLUE);
        tvPreview.setVisibility(View.GONE);
        tvPreview.setMovementMethod(LinkMovementMethod.getInstance());
        root.addView(tvPreview);

        LinearLayout row = new LinearLayout(this);
        Button btnSave = new Button(this); btnSave.setText("Simpan");
        Button btnToggle = new Button(this); btnToggle.setText("Preview");
        Button btnGraph = new Button(this); btnGraph.setText("Graph");
        row.addView(btnSave); row.addView(btnToggle); row.addView(btnGraph);
        root.addView(row);

        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList) {
            @Override
            public View getView(int pos, View convert, ViewGroup parent) {
                View v = super.getView(pos, convert, parent);
                TextView txt = (TextView) v.findViewById(android.R.id.text1);
                txt.setTextColor(Color.BLACK);
                return v;
            }
        };
        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        root.addView(lv);

        Runnable refresh = () -> {
            String res = listVaultFiles(getVaultFolder().getAbsolutePath());
            fileList.clear();
            if(!res.equals("Kosong")) for(String f : res.split("\\|")) fileList.add(f);
            adapter.notifyDataSetChanged();
        };

        btnSave.setOnClickListener(v -> {
            String t = etTitle.getText().toString();
            if(t.isEmpty()) return;
            if(!t.endsWith(".md")) t += ".md";
            saveMarkdownNative(new File(getVaultFolder(), t).getAbsolutePath(), etBody.getText().toString());
            refresh.run();
            Toast.makeText(this, "Tersimpan di Documents/OdfizVault", Toast.LENGTH_SHORT).show();
        });

        btnToggle.setOnClickListener(v -> {
            if(tvPreview.getVisibility() == View.GONE) {
                String html = renderMarkdownNative(etBody.getText().toString());
                SpannableStringBuilder ssb = new SpannableStringBuilder(Html.fromHtml(html));
                URLSpan[] spans = ssb.getSpans(0, ssb.length(), URLSpan.class);
                for (URLSpan span : spans) {
                    int start = ssb.getSpanStart(span);
                    int end = ssb.getSpanEnd(span);
                    String target = span.getURL();
                    ssb.setSpan(new ClickableSpan() {
                        @Override public void onClick(View w) { openFile(target); }
                    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.removeSpan(span);
                }
                tvPreview.setText(ssb);
                tvPreview.setVisibility(View.VISIBLE); etBody.setVisibility(View.GONE);
            } else {
                tvPreview.setVisibility(View.GONE); etBody.setVisibility(View.VISIBLE);
            }
        });

        btnGraph.setOnClickListener(v -> showGraph());
        lv.setOnItemClickListener((p, v, pos, id) -> openFile(fileList.get(pos)));

        refresh.run();
        setContentView(root);
    }

    private void openFile(String name) {
        if(!name.endsWith(".md")) name += ".md";
        File f = new File(getVaultFolder(), name);
        etTitle.setText(name);
        if(f.exists()) etBody.setText(readMarkdownNative(f.getAbsolutePath()));
        else etBody.setText("");
        tvPreview.setVisibility(View.GONE);
        etBody.setVisibility(View.VISIBLE);
    }

    private void showGraph() {
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
                    case MotionEvent.ACTION_DOWN:
                        lastX = ev.getX(); lastY = ev.getY();
                        break;
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

        Button btnX = new Button(this); btnX.setText("Tutup");
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(250, 150);
        lp.gravity = Gravity.TOP | Gravity.RIGHT;
        btnX.setLayoutParams(lp);
        btnX.setOnClickListener(v -> d.dismiss());
        layout.addView(btnX);

        d.setContentView(layout);
        d.show();
    }
}
