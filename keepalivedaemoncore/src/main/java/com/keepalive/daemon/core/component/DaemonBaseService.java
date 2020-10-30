package com.keepalive.daemon.core.component;

import android.app.Service;
import android.content.Intent;

public abstract class DaemonBaseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent().setClassName(getPackageName(), AssistService1.class.getName()));
        startService(new Intent().setClassName(getPackageName(), AssistService2.class.getName()));
        startService(new Intent().setClassName(getPackageName(), DaemonService.class.getName()));
    }
}
