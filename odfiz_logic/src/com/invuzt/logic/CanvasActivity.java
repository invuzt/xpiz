package com.invuzt.logic;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.widget.FrameLayout;
import java.util.ArrayList;

public class CanvasActivity extends Activity {
    private FrameLayout canvas;
    private Paint linePaint;
    private ArrayList<NodeView> nodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        canvas = new FrameLayout(this) {
            @Override
            protected void onDraw(Canvas c) {
                super.onDraw(c);
                drawWires(c); // Gambar kabel antar node
            }
        };
        canvas.setBackgroundColor(Color.parseColor("#121212")); // Grid Dark Mode
        canvas.setWillNotDraw(false);

        linePaint = new Paint();
        linePaint.setColor(Color.CYAN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(8f);
        linePaint.setAntiAlias(true);

        // Tambah Node Contoh
        addNode("PRODUK: DIMSUM", 100, 200, Color.BLUE);
        addNode("TOTALIZER", 500, 400, Color.GREEN);
        addNode("PRINTER", 900, 200, Color.RED);

        setContentView(canvas);
    }

    private void addNode(String name, float x, float y, int color) {
        NodeView nv = new NodeView(this, name, color);
        nv.setX(x);
        nv.setY(y);
        nodes.add(nv);
        canvas.addView(nv, 300, 150);
    }

    private void drawWires(Canvas c) {
        // Logika sederhana: Hubungkan node 0 ke 1, 1 ke 2
        for (int i = 0; i < nodes.size() - 1; i++) {
            NodeView start = nodes.get(i);
            NodeView end = nodes.get(i + 1);

            Path path = new Path();
            float startX = start.getX() + start.getWidth();
            float startY = start.getY() + (start.getHeight() / 2);
            float endX = end.getX();
            float endY = end.getY() + (end.getHeight() / 2);

            // Garis Bezier (Melengkung halus)
            path.moveTo(startX, startY);
            path.cubicTo((startX + endX) / 2, startY, (startX + endX) / 2, endY, endX, endY);
            c.drawPath(path, linePaint);
        }
    }
}
