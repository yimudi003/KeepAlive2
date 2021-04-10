package com.keepalive.daemon.core.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.keepalive.daemon.core.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtil {

    /**
     * 唯一前台通知ID
     */
    public static final int NOTIFICATION_ID = 0x9999;

    public static Notification createNotification(Context context,
                                                  int smallIconId,
                                                  int largeIconId,
                                                  String title,
                                                  String text,
                                                  boolean ongoing,
                                                  int pri,
                                                  int importance,
                                                  CharSequence tickerText,
                                                  PendingIntent pendingIntent,
                                                  RemoteViews views) {
        Logger.d(Logger.TAG, "call createNotification(): smallIconId=" + smallIconId
                + ", largeIconId=" + largeIconId + ", title=" + title + ", text=" + text
                + ", ongoing=" + ongoing + ", pri=" + pri + ", tickerText=" + tickerText
                + ", pendingIntent=" + pendingIntent + ", remoteViews=" + views);
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // 唯一的通知通道的id.
        String channelId = context.getPackageName() + ".notification.channelId";

        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 用户可见的通道名称
            String channelName = context.getPackageName() + ".notification.channelName";
            // 通道的重要程度
            if (importance < 0 || importance > 5) {
                importance = NotificationManager.IMPORTANCE_DEFAULT;
            }
            NotificationChannel nc = new NotificationChannel(channelId, channelName, importance);
            nc.setDescription(context.getPackageName() + ".notification.description");
            if (nm != null) {
                nm.createNotificationChannel(nc);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

        // 设置通知小图标
        if (smallIconId == 0) {
            builder.setSmallIcon(R.drawable.noti_icon);
//            Logger.w(Logger.TAG, "Oops!!! Invalid notification small smallIconId.");
//            return null;
        } else {
            builder.setSmallIcon(smallIconId);
        }

        // 设置通知大图标
        if (largeIconId > 0) {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), largeIconId);
            builder.setLargeIcon(bm);
        }

        // 设置通知标题
        String label = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        if (TextUtils.isEmpty(title)) {
            builder.setContentTitle(label);
        } else {
            builder.setContentTitle(title);
        }

        // 设置通知内容
        if (TextUtils.isEmpty(text)) {
            builder.setContentText(label + "正在运行");
        } else {
            builder.setContentText(text);
        }

        // 设置通知显示的时间
        builder.setWhen(System.currentTimeMillis());

        // 设置是否常驻
        builder.setOngoing(ongoing);

        // 设置优先级
        if (pri >= NotificationCompat.PRIORITY_MIN && pri <= NotificationCompat.PRIORITY_MAX) {
            builder.setPriority(pri);
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        // 设置提示
        if (!TextUtils.isEmpty(tickerText)) {
            builder.setTicker(tickerText);
        }

        // 设置 ContentIntent
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        // 设置自定义布局
        if (views != null) {
            builder.setContent(views);
        }

        // 创建通知并返回
        return builder.build();
    }

    public static void showNotification(Service service, Notification notification) {
        if (notification == null) {
            return;
        }

        try {
            service.startForeground(NOTIFICATION_ID, notification);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
