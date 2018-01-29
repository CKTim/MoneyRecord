package com.example.myapplication;

import android.app.Application;

import com.example.myapplication.utils.SPUtil;
import com.zhouyou.http.EasyHttp;

/**
 * Created by CXK on 2018/1/27.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        EasyHttp.init(this);//默认初始化,必须调用
        SPUtil.newInstance().init(this);//保存用户信息
    }
}
