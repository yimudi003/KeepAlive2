package com.keepalive.daemon.core.component;

import android.content.Intent;
import android.os.IBinder;

import com.keepalive.daemon.core.notification.NotifyResidentService;
import com.keepalive.daemon.core.utils.ServiceHolder;

public class DaemonService extends DaemonBaseService {

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        ServiceHolder.fireService(this, NotifyResidentService.class, true);

        Intent intent2 = new Intent();
        intent2.setClassName(getPackageName(), AssistService1.class.getName());
        startService(intent2);

        Intent intent3 = new Intent();
        intent3.setClassName(getPackageName(), AssistService2.class.getName());
        startService(intent3);
        super.onCreate();
    }
}
