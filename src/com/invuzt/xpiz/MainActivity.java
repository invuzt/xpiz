package com.invuzt.xpiz;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.view.inputmethod.EditorInfo;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {
    static { System.loadLibrary("hello"); }
    private native String predictBestButton(String cmd);

    private LinearLayout productContainer;
    private TextView totalView, reportView;
    private int totalBelanja = 0, totalOmset = 0, transaksiCount = 0;
    private String currentItems = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE); // Layout Putih Bersih

        // 1. Tombol Produk
        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        productContainer = new LinearLayout(this);
        hScroll.addView(productContainer);
        root.addView(hScroll);

        // 2. Display Kasir
        totalView = new TextView(this);
        totalView.setText("Rp 0");
        totalView.setTextColor(Color.BLACK);
        totalView.setTextSize(40);
        totalView.setGravity(Gravity.CENTER);
        root.addView(totalView);

        // 3. Laporan Real-time (Sembunyi/Tampil)
        reportView = new TextView(this);
        reportView.setText("LAPORAN HARI INI: Rp 0 | Transaksi: 0");
        reportView.setTextColor(Color.BLUE);
        root.addView(reportView);

        // 4. Area Struk di Layar
        TextView receiptPreview = new TextView(this);
        receiptPreview.setTypeface(Typeface.MONOSPACE);
        receiptPreview.setBackgroundColor(Color.LTGRAY);
        root.addView(receiptPreview, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        // 5. Input
        EditText input = new EditText(this);
        input.setHint("Nama:Harga atau Nominal Bayar");
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        root.addView(input);

        input.setOnEditorActionListener((v, id, event) -> {
            String txt = input.getText().toString();
            if(!txt.isEmpty()){
                String res = predictBestButton(txt);
                if(res.startsWith("ADD")) {
                    String[] p = res.split("\\|");
                    addBtn(p[1], Integer.parseInt(p[2]));
                } else if(res.startsWith("PAY_CUSTOM")) {
                    finalizeSale(Integer.parseInt(res.split("\\|")[1]), receiptPreview);
                }
                input.setText("");
            }
            return true;
        });
        setContentView(root);
    }

    private void addBtn(String n, int h) {
        Button b = new Button(this);
        b.setText(n);
        b.setOnClickListener(v -> {
            totalBelanja += h;
            currentItems += n + "  Rp" + h + "\n";
            totalView.setText("Rp " + totalBelanja);
        });
        productContainer.addView(b);
    }

    private void finalizeSale(int bayar, TextView preview) {
        String noAntrian = predictBestButton("get_next_queue");
        String time = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(new Date());
        
        StringBuilder struk = new StringBuilder();
        struk.append("      ODFIZ PONOROGO\n");
        struk.append("--------------------------\n");
        struk.append("ANTRIAN : #").append(noAntrian).append("\n");
        struk.append("WAKTU   : ").append(time).append("\n");
        struk.append("--------------------------\n");
        struk.append(currentItems);
        struk.append("--------------------------\n");
        struk.append("TOTAL   : Rp ").append(totalBelanja).append("\n");
        struk.append("BAYAR   : Rp ").append(bayar).append("\n");
        struk.append("KEMBALI : Rp ").append(bayar - totalBelanja).append("\n");
        struk.append("--------------------------\n");
        struk.append("   TERIMA KASIH LUR!\n");

        preview.setText(struk.toString());
        
        // Simpan ke Laporan
        totalOmset += totalBelanja;
        transaksiCount++;
        reportView.setText("OMSET: Rp " + totalOmset + " | TX: " + transaksiCount);

        // Reset Kasir
        totalBelanja = 0;
        currentItems = "";
        totalView.setText("Rp 0");
        
        saveAsPdf(struk.toString(), noAntrian);
    }

    private void saveAsPdf(String content, String no) {
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setTypeface(Typeface.MONOSPACE);

        int y = 50;
        for (String line : content.split("\n")) {
            canvas.drawText(line, 20, y, paint);
            y += 20;
        }
        doc.finishPage(page);
        
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Struk_Odfiz_"+no+".pdf");
        try {
            doc.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Struk PDF Disimpan!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) { e.printStackTrace(); }
        doc.close();
    }
}
