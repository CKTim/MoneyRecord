package com.example.myapplication.activity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;

import com.example.myapplication.R;
import com.example.myapplication.bean.PersonPayResultBean;
import com.example.myapplication.bean.PersonTojsonBean;
import com.example.myapplication.impl.AndroidInterface;
import com.example.myapplication.retrofit.RetrofitBase;
import com.example.myapplication.utils.SPUtil;
import com.example.myapplication.utils.SignUtil;
import com.google.gson.Gson;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout mContainer;
    private AgentWeb mAgentWeb;

    //广播
    private LocalBroadcastManager mLocalBroadcastManager;
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        mContainer = findViewById(R.id.container);
        //初始化AgentWeb
        mAgentWeb = AgentWeb.with(LoginActivity.this)//传入Activity or Fragment
                .setAgentWebParent(mContainer, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DERECT)
                .createAgentWeb()
                .ready()
                .go("http://139.199.195.194:9002/person/login.jsp");

        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, this));

        //初始化广播
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter("MoneyRecordService"));

        gson = new Gson();
    }

    /**
     * *********************************生命周期**********************************************
     */
    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        //判断服务是否开启
        if (!isAccessibilityServiceEnabled()) {
            jumpToAccessibilitySetting();
        } else if (!isNotificationListenerEnabled()) {
            jumpToNotificationListenSetting();
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重写返回键
     */
    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    /**
     * *********************************接受来自service的广播**********************************************
     */
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("dasdas","mBroadcastReceiver2");
            String money = intent.getStringExtra("money");
            String time = intent.getStringExtra("time");
            String type = intent.getStringExtra("type");

            if(TextUtils.isEmpty(money)){
                return;
            }
            Log.e("dasdas","mBroadcastReceiver3");
            //开始上传数据
            sendData(type, money);
        }
    };

    /**
     * 开始上传数据
     */
    private void sendData(String type, String money) {
        String merNo= (String) SPUtil.newInstance().get("merNo","");
        String subMerNo= (String) SPUtil.newInstance().get("subMerNo","");
        String key= (String) SPUtil.newInstance().get("key","");

        Log.e("dasdas","mBroadcastReceiver4");
        if(TextUtils.isEmpty(merNo)||TextUtils.isEmpty(subMerNo)||TextUtils.isEmpty(key)){
            Log.e("dasdas","mBroadcastReceiver5");
            return;
        }

        //封装json
        PersonTojsonBean mPersonTojsonBean = new PersonTojsonBean();
        mPersonTojsonBean.setMerNo(merNo);
        mPersonTojsonBean.setSubMerNo(subMerNo);
        mPersonTojsonBean.setNetway(type);
        mPersonTojsonBean.setAmount(money);
        String sign = gson.toJson(mPersonTojsonBean, PersonTojsonBean.class);
        mPersonTojsonBean.setSign(SignUtil.jsonToMd5(sign,key).toUpperCase());//签名
        String data = gson.toJson(mPersonTojsonBean, PersonTojsonBean.class);
        Log.e("dada",money+"---");

        //开始请求
        Call<PersonPayResultBean> call= RetrofitBase.getService().postPersonPay(data);
        call.enqueue(new Callback<PersonPayResultBean>() {
            @Override
            public void onResponse(Call<PersonPayResultBean> call, Response<PersonPayResultBean> response) {
                Log.e("dasdas","success");
            }

            @Override
            public void onFailure(Call<PersonPayResultBean> call, Throwable t) {
                Log.e("dasdas","失败了"+t.toString());
            }
        });
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
}
