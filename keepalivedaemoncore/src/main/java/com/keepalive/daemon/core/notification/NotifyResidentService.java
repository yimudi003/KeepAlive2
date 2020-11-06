package com.keepalive.daemon.core.notification;

import android.app.Notification;
import android.content.Intent;

import com.keepalive.daemon.core.Constants;
import com.keepalive.daemon.core.KeepAliveService;
import com.keepalive.daemon.core.component.DaemonService;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.NotificationUtil;
import com.keepalive.daemon.core.utils.ServiceHolder;

public class NotifyResidentService extends KeepAliveService {

    @Override
    public final void onCreate() {
        super.onCreate();
        ServiceHolder.fireService(this, DaemonService.class, false);
        doStart();
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(Logger.TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ " +
                "intent: " + intent + ", startId: " + startId);

        doStartCommand(intent, flags, startId);

        Notification noti = NotificationUtil.createNotification(
                this,
                intent.getIntExtra(Constants.NOTIFICATION_ICON, 0),
                intent.getStringExtra(Constants.NOTIFICATION_TITLE),
                intent.getStringExtra(Constants.NOTIFICATION_TEXT),
                intent.getStringExtra(Constants.NOTIFICATION_ACTIVITY)
        );
        NotificationUtil.showNotification(this, noti);

        ServiceHolder.getInstance().bindService(this, DaemonService.class, null);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public final void onDestroy() {
        doRelease();
        super.onDestroy();
    }

    protected void doStart() {
    }

    protected void doStartCommand(Intent intent, int flags, int startId) {
    }

    protected void doRelease() {
    }
}
