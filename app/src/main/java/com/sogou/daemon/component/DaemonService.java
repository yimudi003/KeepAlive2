package com.sogou.daemon.component;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.sogou.interestclean.notification.NotifyResidentService;

public class DaemonService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), NotifyResidentService.class.getName());
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        Intent intent2 = new Intent();
        intent2.setClassName(getPackageName(), AssistService1.class.getName());
        Intent intent3 = new Intent();
        intent3.setClassName(getPackageName(), AssistService2.class.getName());
        startService(intent2);
        startService(intent3);
    }
}
