package com.invuzt.xpiz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout Utama (Light Mode)
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(50, 100, 50, 50);

        // Jam
        TextView tvClock = new TextView(this);
        tvClock.setText(new java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        tvClock.setTextSize(60);
        tvClock.setTextColor(Color.BLACK);
        tvClock.setGravity(Gravity.CENTER);
        root.addView(tvClock);

        TextView tvLabel = new TextView(this);
        tvLabel.setText("LAWNFIZ");
        tvLabel.setGravity(Gravity.CENTER);
        tvLabel.setTextColor(Color.GRAY);
        root.addView(tvLabel);

        // Spacer
        View spacer = new View(this);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, 100);
        root.addView(spacer, sp);

        // Tombol Atur Default Launcher
        Button btnSetDefault = new Button(this);
        btnSetDefault.setText("SET DEFAULT LAUNCHER");
        btnSetDefault.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
            startActivity(intent);
        });
        root.addView(btnSetDefault);

        // Daftar Aplikasi (App Drawer Langsung di Bawah)
        TextView tvTitle = new TextView(this);
        tvTitle.setText("\nSemua Aplikasi:");
        tvTitle.setTextColor(Color.BLACK);
        root.addView(tvTitle);

        ListView lvApps = new ListView(this);
        final List<ResolveInfo> pkgList = getApps();
        
        List<String> appNames = new ArrayList<>();
        for (ResolveInfo ri : pkgList) {
            appNames.add(ri.loadLabel(getPackageManager()).toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appNames) {
            @Override
            public View getView(int pos, View convert, ViewGroup parent) {
                View v = super.getView(pos, convert, parent);
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                return v;
            }
        };

        lvApps.setAdapter(adapter);
        lvApps.setOnItemClickListener((p, v, pos, id) -> {
            String pkg = pkgList.get(pos).activityInfo.packageName;
            Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
            if(i != null) startActivity(i);
        });

        root.addView(lvApps);
        setContentView(root);
    }

    private List<ResolveInfo> getApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(mainIntent, 0);
        Collections.sort(list, new ResolveInfo.DisplayNameComparator(getPackageManager()));
        return list;
    }

    // Mencegah tombol back menutup launcher
    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
