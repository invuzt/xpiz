package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.ClickableSpan;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {
    static { try { System.loadLibrary("hello"); } catch (UnsatisfiedLinkError e) {} }

    private native String checkRustConnection();
    private native String saveMarkdownNative(String path, String content);
    private native String listVaultFiles(String dirPath);
    private native String readMarkdownNative(String path);
    private native String renderMarkdownNative(String content);

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
        etTitle.setHintTextColor(Color.DKGRAY);
        root.addView(etTitle);

        etBody = new EditText(this);
        etBody.setHint("Tulis isi catatan (gunakan [[Link]] untuk menghubungkan)...");
        etBody.setLines(8);
        etBody.setTextColor(Color.WHITE);
        etBody.setHintTextColor(Color.DKGRAY);
        etBody.setGravity(Gravity.TOP);
        root.addView(etBody);

        tvPreview = new TextView(this);
        tvPreview.setTextColor(Color.WHITE);
        tvPreview.setLinkTextColor(Color.parseColor("#62b5ff"));
        tvPreview.setVisibility(View.GONE);
        tvPreview.setMovementMethod(LinkMovementMethod.getInstance());
        root.addView(tvPreview);

        LinearLayout btnRow = new LinearLayout(this);
        Button btnSave = new Button(this); btnSave.setText("SIMPAN");
        Button btnToggle = new Button(this); btnToggle.setText("PREVIEW");
        btnRow.addView(btnSave); btnRow.addView(btnToggle);
        root.addView(btnRow);

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
                        @Override
                        public void onClick(View widget) {
                            openFile(target);
                        }
                    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.removeSpan(span);
                }
                tvPreview.setText(ssb);
                tvPreview.setVisibility(View.VISIBLE);
                etBody.setVisibility(View.GONE);
            } else {
                tvPreview.setVisibility(View.GONE);
                etBody.setVisibility(View.VISIBLE);
            }
        });

        lv.setOnItemClickListener((p, v, pos, id) -> openFile(fileList.get(pos)));

        refresh.run();
        setContentView(root);
    }

    private void openFile(String name) {
        if(!name.endsWith(".md")) name += ".md";
        File f = new File(getExternalFilesDir(null), name);
        etTitle.setText(name);
        if(f.exists()) {
            etBody.setText(readMarkdownNative(f.getAbsolutePath()));
        } else {
            etBody.setText("");
            Toast.makeText(this, "Catatan baru dibuat", Toast.LENGTH_SHORT).show();
        }
        tvPreview.setVisibility(View.GONE);
        etBody.setVisibility(View.VISIBLE);
    }
}
