package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String checkRustConnection();
    private native String saveMarkdownNative(String path, String content);
    private native String listVaultFiles(String dirPath);

    private ArrayAdapter<String> adapter;
    private ArrayList<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 30, 30, 30);

        // Header
        TextView header = new TextView(this);
        header.setText("xpiz Vault Explorer");
        header.setTextSize(20);
        root.addView(header);

        // Editor Ringkas
        EditText etTitle = new EditText(this);
        etTitle.setHint("Judul.md");
        root.addView(etTitle);

        EditText etBody = new EditText(this);
        etBody.setHint("Isi catatan...");
        root.addView(etBody);

        Button btnSave = new Button(this);
        btnSave.setText("SIMPAN");
        root.addView(btnSave);

        // List File
        TextView label = new TextView(this);
        label.setText("\nCatatan Tersimpan:");
        root.addView(label);

        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        root.addView(lv);

        // Fungsi Refresh List
        Runnable refreshList = () -> {
            try {
                String rawFiles = listVaultFiles(getExternalFilesDir(null).getAbsolutePath());
                fileList.clear();
                if (!rawFiles.equals("Kosong")) {
                    for (String f : rawFiles.split("\\|")) fileList.add(f);
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {}
        };

        btnSave.setOnClickListener(v -> {
            String path = new File(getExternalFilesDir(null), etTitle.getText().toString()).getAbsolutePath();
            saveMarkdownNative(path, etBody.getText().toString());
            refreshList.run();
            etTitle.setText(""); etBody.setText("");
        });

        refreshList.run(); // Load awal
        setContentView(root);
    }
}
