package com.keepalive.daemon.core.service;

import android.content.Intent;

import com.keepalive.daemon.core.KeepAliveService;
import com.keepalive.daemon.core.utils.Logger;

public class DaemonService2 extends KeepAliveService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(Logger.TAG, "call onStartCommand(): intent=" + intent);
        return super.onStartCommand(intent, flags, startId);
    }
}
