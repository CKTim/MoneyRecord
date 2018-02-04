package com.example.myapplication.retrofit;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 简单分装的Retrofit初始化用的基类
 */

public class RetrofitBase {
    private static final String BaseUrl ="http://139.199.195.194:8080";
    private static RetrofitBase sRetrofitBase;

    private RetrofitBase(){
    }

    public static RetrofitBase newInstance() {
        if (sRetrofitBase == null) {
            synchronized (RetrofitBase.class){
                if (sRetrofitBase == null) {
                    sRetrofitBase = new RetrofitBase();
                }
            }
        }
        return sRetrofitBase;
    }

    private RetrofitAPI getRetrofitAPI() {
        //日志拦截器
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//连接超时
                .readTimeout(10, TimeUnit.SECONDS)//读取超时
                .writeTimeout(10, TimeUnit.SECONDS);//写入超时

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("retrofit",message);
            }
        });

        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpBuilder.addInterceptor(interceptor);

        //设置Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)//请求基路径
                .client(okhttpBuilder.build())//添加OkHttpClient
                .addConverterFactory(GsonConverterFactory.create())//添加Gson转换器
                .build();

        return retrofit.create(RetrofitAPI.class);
    }

    public static RetrofitAPI getService() {
        return RetrofitBase.newInstance().getRetrofitAPI();
    }

}
