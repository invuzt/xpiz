package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.text.Html;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {
    static { try { System.loadLibrary("hello"); } catch (UnsatisfiedLinkError e) {} }

    private native String checkRustConnection();
    private native String saveMarkdownNative(String path, String content);
    private native String listVaultFiles(String dirPath);
    private native String readMarkdownNative(String path);
    private native String renderMarkdownNative(String content);

    private ArrayAdapter<String> adapter;
    private ArrayList<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 30, 30, 30);

        TextView status = new TextView(this);
        status.setText(checkRustConnection());
        root.addView(status);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Judul.md");
        root.addView(etTitle);

        final EditText etBody = new EditText(this);
        etBody.setHint("Isi catatan...");
        etBody.setLines(8);
        etBody.setGravity(Gravity.TOP);
        root.addView(etBody);

        final TextView tvPreview = new TextView(this);
        tvPreview.setVisibility(View.GONE);
        root.addView(tvPreview);

        LinearLayout btnRow = new LinearLayout(this);
        Button btnSave = new Button(this); btnSave.setText("SIMPAN");
        Button btnToggle = new Button(this); btnToggle.setText("PREVIEW");
        btnRow.addView(btnSave); btnRow.addView(btnToggle);
        root.addView(btnRow);

        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
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
            String title = etTitle.getText().toString();
            if(title.isEmpty()) return;
            if(!title.endsWith(".md")) title += ".md";
            File f = new File(getExternalFilesDir(null), title);
            saveMarkdownNative(f.getAbsolutePath(), etBody.getText().toString());
            refresh.run();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        });

        btnToggle.setOnClickListener(v -> {
            if(tvPreview.getVisibility() == View.GONE) {
                tvPreview.setText(Html.fromHtml(renderMarkdownNative(etBody.getText().toString())));
                tvPreview.setVisibility(View.VISIBLE);
                etBody.setVisibility(View.GONE);
            } else {
                tvPreview.setVisibility(View.GONE);
                etBody.setVisibility(View.VISIBLE);
            }
        });

        lv.setOnItemClickListener((p, v, pos, id) -> {
            String name = fileList.get(pos);
            String content = readMarkdownNative(new File(getExternalFilesDir(null), name).getAbsolutePath());
            etTitle.setText(name);
            etBody.setText(content);
        });

        refresh.run();
        setContentView(root);
    }
}
