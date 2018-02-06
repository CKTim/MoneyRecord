package com.example.myapplication.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.just.agentweb.AgentWeb;

public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
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
                Toast.makeText(context.getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                Log.e("adsasd","----"+msg);
            }
        });

    }

}
