package com.example.myapplication.activity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.bean.PersonPayResultBean;
import com.example.myapplication.bean.PersonTojsonBean;
import com.example.myapplication.retrofit.RetrofitBase;
import com.example.myapplication.utils.SignUtil;
import com.google.gson.Gson;

import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //view
    private LinearLayout cotainer;
    private TextView tvServiceTip;
    private Button btnLoginOut;
    private Button btnCloseSerVice;

    private boolean isStop = false;

    //广播
    private LocalBroadcastManager mLocalBroadcastManager;

    private Gson gson;

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

        gson = new Gson();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendData("PS_WX", "100");
            }
        },3000);
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
            String type = intent.getStringExtra("type");

            if(TextUtils.isEmpty(money)){
                return;
            }

            //开始上传数据
            sendData(type, money);
        }
    };

    /**
     * 开始上传数据
     */
    private void sendData(String type, String money) {
        //封装json
        PersonTojsonBean mPersonTojsonBean = new PersonTojsonBean();
        mPersonTojsonBean.setMerNo("QYF201705200001");
        mPersonTojsonBean.setSubMerNo("QYF201705200001");
        mPersonTojsonBean.setNetway(type);
        mPersonTojsonBean.setAmount(money);
        mPersonTojsonBean.setDeviceCode("001");
        String sign = gson.toJson(mPersonTojsonBean, PersonTojsonBean.class);
        mPersonTojsonBean.setSign(SignUtil.jsonToMd5(sign).toUpperCase());
        String data = gson.toJson(mPersonTojsonBean, PersonTojsonBean.class);

        //开始请求
        Call<PersonPayResultBean> call=RetrofitBase.getService().postPersonPay(data);
        call.enqueue(new Callback<PersonPayResultBean>() {
            @Override
            public void onResponse(Call<PersonPayResultBean> call, Response<PersonPayResultBean> response) {
                Log.e("dassada",response.body().getMsg());
            }

            @Override
            public void onFailure(Call<PersonPayResultBean> call, Throwable t) {
                Log.e("dassada",t.toString());
            }
        });
    }


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
