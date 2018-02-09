package com.example.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.myapplication.utils.DateUtil;
import com.example.myapplication.utils.SPUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 转账记录Service,继承AccessibilityService
 */
public class MoneyRecordService extends AccessibilityService {

    private boolean isMoney = false;//是否找到了金钱
    private String money;//查询到的金钱

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            //界面跳转的监听
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                //开始在转账界面找时间金额
                findMoney(rootNode);
                break;
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
                    if (!TextUtils.isEmpty(rootNode.getChild(i).getText())) {
                        String text = rootNode.getChild(i).getText().toString();
                        if ("收款金额".equals(text)) {
                            String money = rootNode.getChild(i + 1).getText().toString();
                            if (!TextUtils.isEmpty(money)) {
                                boolean isWeChatNotification = (boolean) SPUtil.newInstance().get("wechat_notification", false);
                                if (isWeChatNotification) {
                                    sendBrocast(money, DateUtil.formatDateTime(System.currentTimeMillis()));
                                    back2Home();
                                    SPUtil.newInstance().putAndApply("wechat_notification", false);
                                }
                                break;
                            }
                        }
                    }
                    findMoney(rootNode.getChild(i));
//                    List<AccessibilityNodeInfo> moneys = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/acg");
//                    String moneyCache = null;
//                    //找到最新一条转账的金钱￥
//                    if (moneys != null && moneys.size() != 0) {
//                        if(!TextUtils.isEmpty(moneys.get(moneys.size() - 1).getText())){
//                            isMoney = true;
//                            moneyCache = moneys.get(moneys.size() - 1).getText().toString();
//                        }
//                    }
//
//                    //找到相应信息了
//                    if (isMoney) {
//                        if (TextUtils.isEmpty(moneyCache)) {
//                            return;
//                        }
//                        Log.e("dasdasd","3");
////                        if (!TextUtils.isEmpty(money) && money.equals(moneyCache)) {
////                            return;
////                        }
////
////                        Log.e("dasdasd","4");
//                        money = moneyCache;
//                        isMoney = false;
//                        sendBrocast(money, DateUtil.formatDateTime(System.currentTimeMillis()));
//                        back2Home();
//                        break;
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("dadasdasd", e.toString());
        }
    }

    /**
     * 发送广播到activity保存转账信息
     */
    public void sendBrocast(String money, String time) {
        BigDecimal mBigDecimal1 = new BigDecimal(money.replace("￥", ""));
        BigDecimal mBigDecimal2 = new BigDecimal("100");

        Intent intent = new Intent("MoneyRecordService");
        intent.putExtra("money", mBigDecimal1.multiply(mBigDecimal2).intValue()+"");
        intent.putExtra("time", time);
        intent.putExtra("type", "PS_WX");
        LocalBroadcastManager.getInstance(MoneyRecordService.this).sendBroadcast(intent);
        Log.e("dasdas", "发送广播");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification builder = new Notification.Builder(this).setContentTitle("提示")
                .setContentText("服务正在运行").setSmallIcon(R.mipmap.logo).setContentIntent(pendingIntent).build();
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
}
