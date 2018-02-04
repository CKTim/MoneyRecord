package com.example.myapplication.retrofit;

import com.example.myapplication.bean.PersonPayResultBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @FormUrlEncoded
    @POST("api/personPayResult.action")
    Call<PersonPayResultBean> postPersonPay(@Field("data") String str);
}
