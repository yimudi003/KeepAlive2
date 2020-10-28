package com.keepalive.daemon.core.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.keepalive.daemon.core.utils.Logger;

public class DaemonProcessService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.v(Logger.TAG, "onCreate");
    }
}
