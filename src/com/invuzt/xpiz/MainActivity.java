package com.invuzt.xpiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.graphics.Color;
import java.util.ArrayList;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }

    private native String getPasswordAdvice(String p);
    private native boolean savePasswordNative(String p);
    private native int getVaultSize();
    private native String getVaultItem(int index);
    private native boolean deletePasswordNative(int index);

    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 40, 40, 40);

        TextView label = new TextView(this);
        label.setText("xpiz Secure Vault (Rust Powered)");
        root.addView(label);

        final EditText input = new EditText(this);
        input.setHint("Ketik password baru...");
        root.addView(input);

        Button btnAdd = new Button(this);
        btnAdd.setText("Add to Rust Vault");
        root.addView(btnAdd);

        ListView listView = new ListView(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        root.addView(listView);

        btnAdd.setOnClickListener(v -> {
            String p = input.getText().toString();
            if(savePasswordNative(p)) {
                updateList();
                input.setText("");
            }
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if(deletePasswordNative(position)) {
                updateList();
                Toast.makeText(this, "Deleted from Rust!", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        setContentView(root);
        updateList();
    }

    void updateList() {
        listItems.clear();
        int size = getVaultSize();
        for(int i=0; i<size; i++) {
            listItems.add("Saved: " + getVaultItem(i));
        }
        adapter.notifyDataSetChanged();
    }
}
