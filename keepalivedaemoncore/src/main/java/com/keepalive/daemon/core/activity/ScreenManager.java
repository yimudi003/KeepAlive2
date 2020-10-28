/**
 * 对1像素Activity进行防止内存泄露的处理，新建一个ScreenManager类
 */
package com.keepalive.daemon.core.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;

public class ScreenManager {

    private WeakReference<Activity> mActivity;

    private ScreenManager() {
    }

    private static class Holder {
        private volatile static ScreenManager INSTANCE = new ScreenManager();
    }

    public static ScreenManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 获得SinglePixelActivity的引用
     *
     * @param activity
     */
    public void setSingleActivity(Activity activity) {
        mActivity = new WeakReference<>(activity);
    }

    /**
     * 启动SinglePixelActivity
     */
    public void startActivity(Context context) {
        Intent intent = new Intent(context, SinglePixelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 结束SinglePixelActivity
     */
    public void finishActivity() {
        if (mActivity != null) {
            Activity activity = mActivity.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
