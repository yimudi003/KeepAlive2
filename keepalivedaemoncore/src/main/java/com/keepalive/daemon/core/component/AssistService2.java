package com.keepalive.daemon.core.component;

import android.content.Intent;
import android.os.IBinder;

import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.ServiceHolder;

public class AssistService2 extends DaemonProcessService {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceHolder.getInstance().bindService(this, null);
    }
}
