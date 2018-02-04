package com.example.myapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;

import com.example.myapplication.R;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout mContainer;
    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContainer = findViewById(R.id.container);

        //初始化AgentWeb
        mAgentWeb = AgentWeb.with(LoginActivity.this)//传入Activity or Fragment
                .setAgentWebParent(mContainer, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .addJavascriptInterface("android",new AndroidInterface())
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DERECT)
                .createAgentWeb()
                .ready()
                .go("http://www.jd.com");

    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause(); //暂停应用内所有 WebView ， 需谨慎。
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
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
     * js调用android方法
     */
    public class AndroidInterface{
        @JavascriptInterface
        public void saveToken(String token){
            Log.e("dasdasd" , "saveToken");
        }
    }
}
