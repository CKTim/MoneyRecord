package com.example.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.utils.DateUtil;

import java.util.List;

/**
 * 抢红包Service,继承AccessibilityService
 */
public class MoneyRecordService extends AccessibilityService {

    private boolean isScreenOn;//用于判断是否屏幕是亮着的
    private PowerManager.WakeLock wakeLock;//获取PowerManager.WakeLock对象
    private KeyguardManager.KeyguardLock keyguardLock;//KeyguardManager.KeyguardLock对象

    /**
     * 微信相关
     */
    boolean isTime = false;//是否找到了时间
    private boolean isMoney = false;//是否找到了金钱
    private String time;//查询到的时间
    private String money;//查询到的金钱

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
//            //通知栏来信息，判断是否含有微信红包字样，是的话跳转
//            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
//                Log.e("dgsajdh", "TYPE_NOTIFICATION_STATE_CHANGED");
//                List<CharSequence> texts = event.getText();
//                for (CharSequence text : texts) {
//                    String content = text.toString();
//                    if (!TextUtils.isEmpty(content)) {
//                        //判断是否含有[到账通知]字样
//                        Log.e("dgsajdh", content);
//                        //微信
//                        if (content.contains("收款到账通知") || content.contains("微信支付收款")) {
//                            if (!isScreenOn()) {
//                                wakeUpScreen();
//                            }
//                            //打开微信转账页面
//                            openWeChatPage(event);
//                        }
//
//                        //支付宝
//                        if (content.contains("通过扫码向你付款")) {
//                            //直接保存下来即可
//                            sendBrocast(content, DateUtil.formatDateTime(System.currentTimeMillis()));
//                        }
//                    }
//                }
//                break;
            //界面跳转的监听
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                //开始在转账界面找时间金额
                findMoney(rootNode);
                break;
        }
    }

    /**
     * 打开微信转账页面
     */
    private void openWeChatPage(AccessibilityEvent event) {
        //A instanceof B 用来判断内存中实际对象A是不是B类型，常用于强制转换前的判断
        try {
            if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                Notification notification = (Notification) event.getParcelableData();
                //打开微信转账页面
                PendingIntent pendingIntent = notification.contentIntent;
                pendingIntent.send();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始在转账界面找时间金额
     */
    private void findMoney(AccessibilityNodeInfo rootNode) {
        try {
            if (rootNode != null) {
                //从最后一行开始找起
                for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
                    List<AccessibilityNodeInfo> times = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/acd");
                    List<AccessibilityNodeInfo> moneys = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/acj");
                    String timeCache = null;
                    String moneyCache = null;
                    //找到最新一条转账的时间
                    if (times != null && times.size() != 0) {
                        isTime = true;
                        timeCache = times.get(times.size() - 1).getText().toString();
                    }

                    //找到最新一条转账的金钱
                    if (moneys != null && moneys.size() != 0) {
                        isMoney = true;
                        moneyCache = moneys.get(moneys.size() - 1).getText().toString();
                    }

                    //找到相应信息了
                    if (isMoney && isTime) {
                        if (!TextUtils.isEmpty(time) && !TextUtils.isEmpty(money) && time.equals(timeCache) && money.equals(moneyCache)) {
                            return;
                        }

                        if (TextUtils.isEmpty(timeCache) && TextUtils.isEmpty(moneyCache)) {
                            return;
                        }

                        time = timeCache;
                        money = moneyCache;
                        isMoney = false;
                        isTime = false;
                        sendBrocast(money, time);
                        back2Home();
                        release();
                        break;
                    }
                }
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
        LocalBroadcastManager.getInstance(MoneyRecordService.this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification builder = new Notification.Builder(this).setContentTitle("提示")
                .setContentText("服务正在运行").setSmallIcon(R.mipmap.ic_launcher).build();
        startForeground(1, builder);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 服务连接
     */
    @Override
    protected void onServiceConnected() {
        Log.e("dadad", "服务开启");
        super.onServiceConnected();
    }

    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {
    }

    /**
     * 服务断开
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * 返回桌面
     */
    private void back2Home() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
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
