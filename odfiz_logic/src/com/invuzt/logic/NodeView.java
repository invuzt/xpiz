package com.invuzt.logic;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.TextView;

public class NodeView extends FrameLayout {
    private float dX, dY;
    public String nodeName;
    public int color = Color.DKGRAY;

    public NodeView(Context context, String name, int color) {
        super(context);
        this.nodeName = name;
        this.color = color;
        
        setBackgroundColor(color);
        setPadding(20, 20, 20, 20);
        
        TextView tv = new TextView(context);
        tv.setText(name);
        tv.setTextColor(Color.WHITE);
        addView(tv);

        // Logika Drag & Drop
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    v.animate().x(event.getRawX() + dX).y(event.getRawY() + dY).setDuration(0).start();
                    ((View)getParent()).invalidate(); // Gambar ulang kabel saat digeser
                    break;
            }
            return true;
        });
    }
}
