package com.example.myapplication.activity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private LinearLayout cotainer;
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cotainer = findViewById(R.id.cotainer);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter("MoneyRecordService"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断服务是否开启
        if (!isStartAccessibilityService()) {
            jumpToAccessibilitySetting();
        }else if(!isNotificationListenerEnabled()){
            openNotificationListenSettings();
        }
    }

    //接受来自service的广播
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String money = intent.getStringExtra("money");
            String time = intent.getStringExtra("time");
            TextView tv = new TextView(MainActivity.this);
            tv.setTextSize(14);
            tv.setTextColor(Color.BLACK);
            tv.setText(money + "---" + time);
            Log.e("AAAAA", money + "---" + time);
            cotainer.addView(tv);
        }
    };

    /**
     * 重写返回键
     */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    /**
     * 判断AccessibilityService服务是否已经启动
     */
    public boolean isStartAccessibilityService() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : serviceInfos) {
            String id = info.getId();
            if (id.contains("RecordService")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 跳转到AccessibilityService设置页面
     */
    public void jumpToAccessibilitySetting() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    /**
     * 判断NotificationListener服务是否已经启动
     */
    public boolean isNotificationListenerEnabled() {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(getPackageName())) {
            return true;
        }
        return false;
    }

    public void openNotificationListenSettings() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
