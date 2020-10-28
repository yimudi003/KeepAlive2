package com.keepalive.daemon.core.component;

import android.content.Intent;
import android.os.IBinder;

public class AssistService1 extends DaemonProcessService {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
