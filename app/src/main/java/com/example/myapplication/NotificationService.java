package com.example.myapplication;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.myapplication.utils.DateUtil;
import com.example.myapplication.utils.SPUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by CXK on 2018/1/29.
 */

public class NotificationService extends NotificationListenerService {
    private boolean isScreenOn;//用于判断是否屏幕是亮着的
    private PowerManager.WakeLock wakeLock;//获取PowerManager.WakeLock对象
    private KeyguardManager.KeyguardLock keyguardLock;//KeyguardManager.KeyguardLock对象

    private String mTencentPackage = "com.tencent.mm";
    private String mAliPackage = "com.eg.android.AlipayGphone";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // 如果该通知的包名不是微信，那么 pass 掉
        if (!mTencentPackage.equals(sbn.getPackageName()) && !mAliPackage.equals(sbn.getPackageName())) {
            return;
        }
        try {
            Notification notification = sbn.getNotification();
            if (notification == null) {
                return;
            }
            PendingIntent pendingIntent = null;
            // 当 API > 18 时，使用 extras 获取通知的详细信息
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Bundle extras = notification.extras;
                if (extras != null) {
                    // 获取通知内容
                    String content = extras.getString(Notification.EXTRA_TEXT, "");
                    if (!TextUtils.isEmpty(content)) {
                        //微信
                        if (content.contains("收款到账通知") || content.contains("微信支付收款")) {
                            //微信转账通知栏
                            SPUtil.newInstance().putAndApply("wechat_notification",true);
                            pendingIntent = notification.contentIntent;
                            //如果屏幕锁着，则开启屏幕
                            if (!isScreenOn()) {
                                wakeUpScreen();
                            }
                        }

                        //支付宝
                        if (content.contains("通过扫码向你付款")) {
                            //直接保存下来即可
                            sendBrocast(content, DateUtil.formatDateTime(System.currentTimeMillis()));
                        }
                    }
                }
            }
            // 发送 pendingIntent 以此打开微信
            if (pendingIntent != null) {
                pendingIntent.send();
                release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送广播到activity保存转账信息
     */
    public void sendBrocast(String money, String time) {
        if (TextUtils.isEmpty(money)) {
            return;
        }
        BigDecimal mBigDecimal1 = new BigDecimal(money.substring(money.indexOf("向你付款")+4, money.length()-1));
        BigDecimal mBigDecimal2 = new BigDecimal("100");

        Intent intent = new Intent("MoneyRecordService");
        intent.putExtra("money",mBigDecimal1.multiply(mBigDecimal2).intValue()+"");
        intent.putExtra("time", time);
        intent.putExtra("type", "PS_ZFB");
        LocalBroadcastManager.getInstance(NotificationService.this).sendBroadcast(intent);
    }


    /**
     * 判断是否处于亮屏状态
     *
     * @return true-亮屏，false-暗屏
     */
    private boolean isScreenOn() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        isScreenOn = pm.isScreenOn();
        Log.e("isScreenOn", isScreenOn + "");
        return isScreenOn;
    }

    /**
     * 解锁屏幕
     */
    private void wakeUpScreen() {

        //获取电源管理器对象
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //后面的参数|表示同时传入两个值，最后的是调试用的Tag
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "bright");

        //点亮屏幕
        wakeLock.acquire();

        //得到键盘锁管理器
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = km.newKeyguardLock("unlock");

        //解锁
        keyguardLock.disableKeyguard();
    }

    /**
     * 释放keyguardLock和wakeLock
     */
    public void release() {
        if (keyguardLock != null) {
            keyguardLock.reenableKeyguard();
            keyguardLock = null;
        }
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

}
