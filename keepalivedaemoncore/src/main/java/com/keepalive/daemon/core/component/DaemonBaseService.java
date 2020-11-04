package com.keepalive.daemon.core.component;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.keepalive.daemon.core.IMonitorService;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.NotificationUtil;

public abstract class DaemonBaseService extends Service {

    private IMonitorService.Stub binder = new IMonitorService.Stub() {
        @Override
        public void processMessage(Bundle bundle) throws RemoteException {
            DaemonBaseService.this.processMessage(bundle);
        }
    };

    private void processMessage(Bundle bundle) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(Logger.TAG, "############### intent: " + intent + ", startId: " + startId);

//        Notification noti = NotificationUtil.createNotification(
//                this,
//                intent.getIntExtra("noti_icon", 0),
//                intent.getStringExtra("noti_title"),
//                intent.getStringExtra("noti_text"),
//                intent.getStringExtra("noti_activity")
//        );
//        NotificationUtil.showNotification(this, noti);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
