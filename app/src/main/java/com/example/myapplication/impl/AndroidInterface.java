package com.example.myapplication.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.example.myapplication.bean.PersonPaySaveBean;
import com.example.myapplication.utils.SPUtil;
import com.google.gson.Gson;
import com.just.agentweb.AgentWeb;

public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private Gson gson;
    private AgentWeb agent;
    private Context context;

    public AndroidInterface(AgentWeb agent, Context context) {
        this.agent = agent;
        this.context = context;
    }



    @JavascriptInterface
    public void save(final String msg) {
        deliver.post(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(context.getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
//                Log.e("adsasd","----"+msg);
                //保存相关信息
                if(gson==null){
                    gson=new Gson();
                }

                PersonPaySaveBean mPersonPaySaveBean=gson.fromJson(msg, PersonPaySaveBean.class);
                if(mPersonPaySaveBean!=null){
                    SPUtil.newInstance().putAndApply("merNo",mPersonPaySaveBean.getMerNo());
                    SPUtil.newInstance().putAndApply("subMerNo",mPersonPaySaveBean.getSubMerNo());
                    SPUtil.newInstance().putAndApply("key",mPersonPaySaveBean.getKey());
                }
            }
        });

    }

}
