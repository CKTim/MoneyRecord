package com.example.myapplication;

import android.app.Application;

import com.example.myapplication.utils.SPUtil;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;
import com.zhouyou.http.cache.model.CacheMode;

/**
 * Created by CXK on 2018/1/27.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SPUtil.newInstance().init(this);//保存用户信息

        EasyHttp.init(this);//默认初始化,必须调用
        EasyHttp.getInstance()
                .setBaseUrl("www.baidu.com")//设置全局URL  url只能是域名 或者域名+端口号
                .debug("EasyHttp", true);
    }
}
