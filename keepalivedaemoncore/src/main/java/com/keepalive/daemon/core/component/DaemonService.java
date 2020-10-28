package com.keepalive.daemon.core.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import com.keepalive.daemon.core.notification.NotifyResidentService;
import com.keepalive.daemon.core.utils.Logger;

public class DaemonService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), NotifyResidentService.class.getName());
        try {
            ContextCompat.startForegroundService(this, intent);
        } catch (Throwable th) {
            Logger.e(Logger.TAG, "failed to start foreground service: " + th.getMessage());
        }

        Intent intent2 = new Intent();
        intent2.setClassName(getPackageName(), AssistService1.class.getName());
        startService(intent2);

        Intent intent3 = new Intent();
        intent3.setClassName(getPackageName(), AssistService2.class.getName());
        startService(intent3);
    }
}
