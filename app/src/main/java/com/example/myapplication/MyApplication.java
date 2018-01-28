package com.example.myapplication;

import android.app.Application;

import com.zhouyou.http.EasyHttp;

/**
 * Created by CXK on 2018/1/27.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        EasyHttp.init(this);//默认初始化,必须调用
    }
}
