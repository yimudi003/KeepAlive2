package com.keepalive.daemon.core.notification;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.keepalive.daemon.core.KeepAliveService;
import com.keepalive.daemon.core.component.DaemonService;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.NotificationUtil;
import com.keepalive.daemon.core.utils.ServiceHolder;

public class NotifyResidentService extends KeepAliveService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceHolder.fireService(this, DaemonService.class, false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(Logger.TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ " +
                "intent: " + intent + ", startId: " + startId);

        Notification noti = NotificationUtil.createNotification(
                this,
                intent.getIntExtra("noti_icon", 0),
                intent.getStringExtra("noti_title"),
                intent.getStringExtra("noti_text"),
                intent.getStringExtra("noti_activity")
        );
        NotificationUtil.showNotification(this, noti);

        ServiceHolder.getInstance().bindService(this, DaemonService.class, null);
        return super.onStartCommand(intent, flags, startId);
    }
}
