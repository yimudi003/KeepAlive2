package com.keepalive.daemon.core.component;

import android.content.Intent;
import android.os.IBinder;

import com.keepalive.daemon.core.utils.Logger;

public class AssistService2 extends DaemonProcessService {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
