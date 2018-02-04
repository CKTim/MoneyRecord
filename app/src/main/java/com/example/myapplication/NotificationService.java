package com.example.myapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.myapplication.utils.DateUtil;

import java.util.List;

/**
 * Created by CXK on 2018/1/29.
 */

public class NotificationService extends NotificationListenerService {
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
                    // 获取通知标题
                    String title = extras.getString(Notification.EXTRA_TITLE, "");
                    // 获取通知内容
                    String content = extras.getString(Notification.EXTRA_TEXT, "");
                    if (!TextUtils.isEmpty(content)) {
                        //微信
                        if (content.contains("收款到账通知") || content.contains("微信支付收款")) {
                            pendingIntent = notification.contentIntent;
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送广播到activity保存转账信息
     */
    public void sendBrocast(String money, String time) {
        Intent intent = new Intent("MoneyRecordService");
        intent.putExtra("money", money);
        intent.putExtra("time", time);
        intent.putExtra("type", "PS_ZFB");
        LocalBroadcastManager.getInstance(NotificationService.this).sendBroadcast(intent);
    }

}
