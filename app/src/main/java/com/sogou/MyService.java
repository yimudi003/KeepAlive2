package com.sogou;

import android.content.Intent;

import com.keepalive.daemon.core.notification.NotifyResidentService;
import com.keepalive.daemon.core.utils.Logger;


public class MyService extends NotifyResidentService {

    @Override
    public void doStartCommand(Intent intent, int flags, int startId) {
        Logger.d(Logger.TAG, "intent: " + intent + ", flags: " + flags + ", startId: " + startId);
    }
}
