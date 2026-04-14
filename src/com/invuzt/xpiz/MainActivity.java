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

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(40, 80, 40, 40);

        TextView tvLabel = new TextView(this);
        tvLabel.setText("LAWNFIZ");
        tvLabel.setTextSize(32);
        tvLabel.setGravity(Gravity.CENTER);
        tvLabel.setTextColor(Color.BLACK);
        root.addView(tvLabel);

        Button btnSet = new Button(this);
        btnSet.setText("PILIH SEBAGAI HOME");
        btnSet.setOnClickListener(v -> {
            try {
                // Metode 1: Langsung ke setelan Home (Paling aman)
                Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                startActivity(intent);
            } catch (Exception e) {
                try {
                    // Metode 2: Jika gagal, pancing dengan intent HOME
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(Intent.createChooser(homeIntent, "Pilih Lawnfiz"));
                } catch (Exception e2) {
                    Toast.makeText(this, "Buka Manual: Settings > Apps > Default Apps > Home", Toast.LENGTH_LONG).show();
                }
            }
        });
        root.addView(btnSet);

        ListView lv = new ListView(this);
        final List<ResolveInfo> apps = getApps();
        List<String> names = new ArrayList<>();
        for (ResolveInfo r : apps) names.add(r.loadLabel(getPackageManager()).toString());

        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names) {
            @Override
            public View getView(int p, View c, ViewGroup pg) {
                View v = super.getView(p, c, pg);
                TextView txt = v.findViewById(android.R.id.text1);
                txt.setTextColor(Color.BLACK);
                return v;
            }
        });

        lv.setOnItemClickListener((p, v, pos, id) -> {
            String pkg = apps.get(pos).activityInfo.packageName;
            Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
            if(i != null) startActivity(i);
        });

        root.addView(lv);
        setContentView(root);
    }

    private List<ResolveInfo> getApps() {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> l = getPackageManager().queryIntentActivities(i, 0);
        Collections.sort(l, new ResolveInfo.DisplayNameComparator(getPackageManager()));
        return l;
    }

    @Override
    public void onBackPressed() {
        // Jangan biarkan menutup aplikasi
    }
}
