package com.keepalive.daemon.core.receiver;

import android.content.Context;
import android.content.Intent;

import com.keepalive.daemon.core.KeepAliveConfigs;
import com.keepalive.daemon.core.component.DaemonReceiver;

public class AutoBootReceiver extends DaemonReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (KeepAliveConfigs.bootReceivedListener != null) {
            KeepAliveConfigs.bootReceivedListener.onReceive(context, intent);
        }
//        KeepAlive.launchAlarm(context);
    }
}
