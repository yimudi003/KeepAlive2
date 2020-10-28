package com.keepalive.daemon.core.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.keepalive.daemon.core.component.DaemonBaseService;
import com.keepalive.daemon.core.utils.Logger;

public class NotifyResidentService extends DaemonBaseService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.v(Logger.TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "daemon");
            builder.setContentTitle("Title");
            builder.setContentText("Content");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManagerCompat
                        .from(this)
                        .createNotificationChannel(
                                new NotificationChannel(
                                        "daemon",
                                        "daemon",
                                        NotificationManager.IMPORTANCE_HIGH
                                ));
            }
            startForeground(123456, builder.build());
        } catch (Exception e) {
            Logger.e(Logger.TAG, "error : " + e, e);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
