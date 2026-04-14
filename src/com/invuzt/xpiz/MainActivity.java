package com.invuzt.xpiz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
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

        TextView tv = new TextView(this);
        tv.setText("LAWNFIZ READY");
        tv.setTextSize(24);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);
        root.addView(tv);

        Button btnSet = new Button(this);
        btnSet.setText("PANCING PILIHAN HOME");
        btnSet.setOnClickListener(v -> {
            // Cara paling ampuh: kirim intent HOME tanpa package, 
            // Android akan bingung dan nanya "Mau pakai yang mana?"
            Intent selector = new Intent(Intent.ACTION_MAIN);
            selector.addCategory(Intent.CATEGORY_HOME);
            startActivity(Intent.createChooser(selector, "Pilih Lawnfiz sebagai Home"));
        });
        root.addView(btnSet);

        ListView lv = new ListView(this);
        final List<ResolveInfo> apps = getApps();
        List<String> names = new ArrayList<>();
        for (ResolveInfo r : apps) names.add(r.loadLabel(getPackageManager()).toString());

        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        lv.setOnItemClickListener((p, v, pos, id) -> {
            String pkg = apps.get(pos).activityInfo.packageName;
            startActivity(getPackageManager().getLaunchIntentForPackage(pkg));
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

    @Override public void onBackPressed() {}
}
