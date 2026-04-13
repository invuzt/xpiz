package com.invuzt.xpiz;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
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

    private native String checkRustConnection();
    private native String saveMarkdownNative(String path, String content);
    private native String listVaultFiles(String dirPath);
    private native String readMarkdownNative(String path);
    private native String renderMarkdownNative(String content);
    private native String getGraphDataNative(String dirPath);

    private EditText etTitle, etBody;
    private TextView tvPreview;
    private ArrayList<String> fileList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 30, 30, 30);
        root.setBackgroundColor(Color.parseColor("#1e1e1e"));

        TextView status = new TextView(this);
        status.setText(checkRustConnection());
        status.setTextColor(Color.GRAY);
        root.addView(status);

        etTitle = new EditText(this);
        etTitle.setHint("Judul.md");
        etTitle.setTextColor(Color.WHITE);
        root.addView(etTitle);

        etBody = new EditText(this);
        etBody.setHint("Isi catatan...");
        etBody.setLines(6);
        etBody.setTextColor(Color.WHITE);
        etBody.setGravity(Gravity.TOP);
        root.addView(etBody);

        tvPreview = new TextView(this);
        tvPreview.setTextColor(Color.WHITE);
        tvPreview.setLinkTextColor(Color.CYAN);
        tvPreview.setVisibility(View.GONE);
        tvPreview.setMovementMethod(LinkMovementMethod.getInstance());
        root.addView(tvPreview);

        LinearLayout row = new LinearLayout(this);
        Button btnSave = new Button(this); btnSave.setText("SIMPAN");
        Button btnToggle = new Button(this); btnToggle.setText("PREVIEW");
        Button btnGraph = new Button(this); btnGraph.setText("GRAPH");
        row.addView(btnSave); row.addView(btnToggle); row.addView(btnGraph);
        root.addView(row);

        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList) {
            @Override
            public View getView(int pos, View convert, ViewGroup parent) {
                View v = super.getView(pos, convert, parent);
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(Color.CYAN);
                return v;
            }
        };
        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        root.addView(lv);

        Runnable refresh = () -> {
            String res = listVaultFiles(getExternalFilesDir(null).getAbsolutePath());
            fileList.clear();
            if(!res.equals("Kosong")) for(String f : res.split("\\|")) fileList.add(f);
            adapter.notifyDataSetChanged();
        };

        btnSave.setOnClickListener(v -> {
            String t = etTitle.getText().toString();
            if(t.isEmpty()) return;
            if(!t.endsWith(".md")) t += ".md";
            saveMarkdownNative(new File(getExternalFilesDir(null), t).getAbsolutePath(), etBody.getText().toString());
            refresh.run();
            Toast.makeText(this, "Tersimpan!", Toast.LENGTH_SHORT).show();
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
        File f = new File(getExternalFilesDir(null), name);
        etTitle.setText(name);
        if(f.exists()) etBody.setText(readMarkdownNative(f.getAbsolutePath()));
        else etBody.setText("");
        tvPreview.setVisibility(View.GONE);
        etBody.setVisibility(View.VISIBLE);
    }

    private void showGraph() {
        String data = getGraphDataNative(getExternalFilesDir(null).getAbsolutePath());
        final Dialog d = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        FrameLayout layout = new FrameLayout(this);
        
        View gv = new View(this) {
            @Override protected void onDraw(Canvas c) {
                if(data.equals("Kosong")) return;
                Paint p = new Paint(); p.setColor(Color.CYAN); p.setStrokeWidth(4); p.setAntiAlias(true);
                Paint tp = new Paint(); tp.setColor(Color.WHITE); tp.setTextSize(30);
                Map<String, Point> nodes = new HashMap<>();
                Random r = new Random();
                String[] pairs = data.split("\\|");
                for(String pair : pairs) {
                    String[] parts = pair.split(":");
                    if(parts.length < 2) continue;
                    for(String n : parts) {
                        if(!nodes.containsKey(n)) {
                            int x = r.nextInt(Math.max(1, getWidth() - 300)) + 150;
                            int y = r.nextInt(Math.max(1, getHeight() - 300)) + 150;
                            nodes.put(n, new Point(x, y));
                        }
                    }
                    Point p1 = nodes.get(parts[0]), p2 = nodes.get(parts[1]);
                    if(p1 != null && p2 != null) c.drawLine(p1.x, p1.y, p2.x, p2.y, p);
                }
                for(String n : nodes.keySet()) { 
                    Point pt = nodes.get(n); 
                    c.drawCircle(pt.x, pt.y, 12, p); 
                    c.drawText(n, pt.x+20, pt.y+10, tp); 
                }
            }
        };

        gv.setBackgroundColor(Color.parseColor("#121212"));
        layout.addView(gv);

        Button btnClose = new Button(this);
        btnClose.setText("X"); btnClose.setTextColor(Color.WHITE);
        btnClose.setBackgroundColor(Color.TRANSPARENT);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(150, 150);
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        btnClose.setLayoutParams(params);
        btnClose.setOnClickListener(v -> d.dismiss());
        layout.addView(btnClose);

        d.setContentView(layout);
        d.setCancelable(true);
        d.show();
    }
}
