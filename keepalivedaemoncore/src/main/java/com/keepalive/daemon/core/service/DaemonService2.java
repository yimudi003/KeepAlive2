package com.keepalive.daemon.core.service;

import android.content.Intent;
import android.util.Log;

import com.keepalive.daemon.core.KeepAliveService;

public class DaemonService2 extends KeepAliveService {
    private static final String TAG = "DaemonService2";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "call onStartCommand(): intent=" + intent);
        return super.onStartCommand(intent, flags, startId);
    }
}
