package com.example.appduration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {
    ListView list;
    LocationManager locationManager;
    Boolean LocationPems, UsesPerms;
    Button showStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.list);
        showStats = findViewById(R.id.showstats);

        LocationPems = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        UsesPerms = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED;


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (LocationPems) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (!checkAllowed()) {
            Intent usesAIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usesAIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(usesAIntent);
        }

        showStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAllowed()) {
                    list.setVisibility(View.VISIBLE);
                    showStats.setVisibility(View.GONE);
                    Usage();
                }
            }
        });
    }

    public void Usage() {

        AskLocation();

        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                System.currentTimeMillis() - 1000 * 3600 * 24, System.currentTimeMillis());

        appList = appList.stream().filter(app -> app.getTotalTimeInForeground() > 0).collect(Collectors.toList());

        long total = 0;
        for (UsageStats usageStats : appList) {
            total = total+usageStats.getTotalTimeInForeground();
        }

        ArrayList<Model> ModelList = new ArrayList<>();
        if (appList.size() > 0) {
            for (UsageStats usageStats : appList) {
                String[] packageNames = usageStats.getPackageName().split("\\.");
                String time = convert(usageStats.getTotalTimeInForeground());
                long percentage = ((usageStats.getTotalTimeInForeground() * 100)/total);
                ModelList.add(new Model(packageNames[packageNames.length - 1].trim(), time, percentage));

            }
            Collections.reverse(ModelList);
            Adapter adapter = new Adapter(this, ModelList);
            list.setAdapter(adapter);
        }
    }

    private String convert(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return (hours + " h " + minutes + " m " + seconds + " s");
    }

    public boolean checkAllowed() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void AskLocation() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

    }

}