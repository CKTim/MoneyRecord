package com.example.myapplication.activity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
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
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.MoneyRecordService;
import com.example.myapplication.R;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //view
    private LinearLayout cotainer;
    private TextView tvServiceTip;
    private Button btnLoginOut;
    private Button btnCloseSerVice;

    private boolean isStop = false;

    //广播
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cotainer = findViewById(R.id.cotainer);
        tvServiceTip = findViewById(R.id.tv_service_tip);
        btnLoginOut = findViewById(R.id.btn_logout);
        btnCloseSerVice = findViewById(R.id.btn_closeService);
        btnLoginOut.setOnClickListener(this);
        btnCloseSerVice.setOnClickListener(this);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter("MoneyRecordService"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断服务是否开启
        if (!isAccessibilityServiceEnabled()) {
            jumpToAccessibilitySetting();
        } else if (!isNotificationListenerEnabled()) {
            jumpToNotificationListenSetting();
        }

        //判断服务状态
        if (isAccessibilityServiceEnabled() && isNotificationListenerEnabled()) {
            tvServiceTip.setText("服务已经启动,可以正常使用");
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
     * ********************************点击事件****************************************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                break;
            case R.id.btn_closeService:
                break;
        }
    }


    /**
     * *********************************页面跳转相关**********************************************
     */
    //判断AccessibilityService服务是否已经启动
    public boolean isAccessibilityServiceEnabled() {
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

    //跳转到AccessibilityService设置页面
    public void jumpToAccessibilitySetting() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    //判断NotificationListener服务是否已经启动
    public boolean isNotificationListenerEnabled() {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(getPackageName())) {
            return true;
        }
        return false;
    }

    //跳转到NotificationListene设置页面
    public void jumpToNotificationListenSetting() {
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
}
