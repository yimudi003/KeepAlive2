/**
 * 对广播进行监听，封装为一个ScreenReceiverUtil类，进行锁屏解锁的广播动态注册监听
 */
package com.keepalive.daemon.core.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.keepalive.daemon.core.utils.Logger;

public class ScreenReceiverUtil {
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private ScreenManager mScreenManager;

    public ScreenReceiverUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void startScreenReceiverListener() {
        // 动态启动广播接收器
        this.mScreenReceiver = new ScreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
        mScreenManager = ScreenManager.getInstance();
    }

    public void stopScreenReceiverListener() {
        if (null != mScreenReceiver) {
            mContext.unregisterReceiver(mScreenReceiver);
            mScreenReceiver = null;
        }
        mScreenManager = null;
    }

    public class ScreenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                if (mScreenManager != null) {
                    mScreenManager.startActivity(context);
                }
                Logger.d(Logger.TAG, "start one pixel activity");
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                if (mScreenManager != null) {
                    mScreenManager.finishActivity(); // 解锁
                }
                Logger.d(Logger.TAG, "finish one pixel activity");
            }
        }
    }
}
