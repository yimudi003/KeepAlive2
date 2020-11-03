package com.keepalive.daemon.core.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.keepalive.daemon.core.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtil {

    /**
     * 唯一前台通知ID
     */
    public static final int NOTIFICATION_ID = 0x1000;

    public static Notification createNotification(Context context,
                                                  int icon,
                                                  String title,
                                                  String text,
                                                  String activityName) {
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // 唯一的通知通道的id.
        String channelId = context.getPackageName() + ".notification.channelId";

        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 用户可见的通道名称
            String channelName = context.getPackageName() + ".notification.channelName";
            // 通道的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel nc = new NotificationChannel(channelId, channelName, importance);
            nc.setDescription(context.getPackageName() + ".notification.description");
            if (nm != null) {
                nm.createNotificationChannel(nc);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        // 通知小图标
        if (icon == 0) {
            builder.setSmallIcon(R.drawable.ic_launcher);
        } else {
            builder.setSmallIcon(icon);
        }
        // 通知标题
        String label = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        if (TextUtils.isEmpty(title)) {
            builder.setContentTitle(label);
        } else {
            builder.setContentTitle(title);
        }
        // 通知内容
        if (TextUtils.isEmpty(text)) {
            builder.setContentText(label + "正在运行");
        } else {
            builder.setContentText(text);
        }
        // 设定通知显示的时间
        builder.setWhen(System.currentTimeMillis());
        // 设定启动的内容
        if (!TextUtils.isEmpty(activityName)) {
            try {
                Class<? extends Activity> act = (Class<? extends Activity>) Class.forName(activityName);
                Intent intent = new Intent(context, act);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

        // 创建通知并返回
        return builder.build();
    }

    public static void showNotification(Service service, Notification notification) {
        try {
            service.startForeground(NOTIFICATION_ID, notification);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
