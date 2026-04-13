package com.invuzt.xpiz;

// ... (import yang lama) ...
import android.widget.AdapterView;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String saveMarkdownNative(String path, String content);
    private native String listVaultFiles(String dirPath);
    private native String readMarkdownNative(String path); // Tambahkan ini

    // ... (variabel UI lainnya) ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ... (kode layout dan button save tetap sama) ...

        lv.setOnItemClickListener((parent, view, position, id) -> {
            String fileName = fileList.get(position);
            File file = new File(getExternalFilesDir(null), fileName);
            
            try {
                // Minta Rust buat ambil isinya
                String content = readMarkdownNative(file.getAbsolutePath());
                
                // Masukkan kembali ke editor
                etTitle.setText(fileName);
                etBody.setText(content);
                
                Toast.makeText(this, "Membuka: " + fileName, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memuat file", Toast.LENGTH_SHORT).show();
            }
        });

        // ...
    }
}
