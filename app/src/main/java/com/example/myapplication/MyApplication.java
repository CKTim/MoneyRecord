package com.example.myapplication;

import android.app.Application;

import com.example.myapplication.utils.SPUtil;
/**
 * Created by CXK on 2018/1/27.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SPUtil.newInstance().init(this);//保存用户信息
    }
}
