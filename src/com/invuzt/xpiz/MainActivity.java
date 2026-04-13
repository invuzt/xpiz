package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.Gravity;
import android.view.View;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {
    static { 
        try {
            System.loadLibrary("hello"); 
        } catch (UnsatisfiedLinkError e) {
            // Safety loading
        }
    }

    private native String checkRustConnection();
    private native String saveMarkdownNative(String path, String content);
    private native String listVaultFiles(String dirPath);
    private native String readMarkdownNative(String path);

    private ArrayAdapter<String> adapter;
    private ArrayList<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 30, 30, 30);

        // UI Components
        TextView header = new TextView(this);
        header.setText("xpiz Vault Explorer");
        header.setTextSize(20);
        root.addView(header);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Judul.md");
        root.addView(etTitle);

        final EditText etBody = new EditText(this);
        etBody.setHint("Isi catatan...");
        etBody.setGravity(Gravity.TOP);
        etBody.setLines(10);
        root.addView(etBody);

        Button btnSave = new Button(this);
        btnSave.setText("SIMPAN KE VAULT");
        root.addView(btnSave);

        TextView label = new TextView(this);
        label.setText("\nCatatan Tersimpan (Klik untuk buka):");
        root.addView(label);

        // List View Setup
        fileList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        root.addView(lv);

        // Logic: Refresh List
        Runnable refreshList = () -> {
            try {
                String rawFiles = listVaultFiles(getExternalFilesDir(null).getAbsolutePath());
                fileList.clear();
                if (!rawFiles.equals("Kosong")) {
                    for (String f : rawFiles.split("\\|")) {
                        if (!f.isEmpty()) fileList.add(f);
                    }
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                // Silent fail untuk list
            }
        };

        // Logic: Save
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            if (title.isEmpty()) {
                Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!title.endsWith(".md")) title += ".md";
            
            File file = new File(getExternalFilesDir(null), title);
            String result = saveMarkdownNative(file.getAbsolutePath(), etBody.getText().toString());
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            
            etTitle.setText("");
            etBody.setText("");
            refreshList.run();
        });

        // Logic: Read (On Click List)
        lv.setOnItemClickListener((parent, view, position, id) -> {
            String fileName = fileList.get(position);
            File file = new File(getExternalFilesDir(null), fileName);
            try {
                String content = readMarkdownNative(file.getAbsolutePath());
                etTitle.setText(fileName);
                etBody.setText(content);
                Toast.makeText(this, "Membuka: " + fileName, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memuat file", Toast.LENGTH_SHORT).show();
            }
        });

        refreshList.run(); // Load awal saat aplikasi dibuka
        setContentView(root);
    }
}
