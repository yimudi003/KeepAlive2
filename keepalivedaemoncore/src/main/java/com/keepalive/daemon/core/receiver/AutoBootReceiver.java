package com.keepalive.daemon.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.keepalive.daemon.core.KeepAlive;
import com.keepalive.daemon.core.KeepAliveConfigs;

public class AutoBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AutoBootReceiver", intent.getAction() + "");
        if (KeepAliveConfigs.bootReceivedListener != null) {
            KeepAliveConfigs.bootReceivedListener.onReceive(context, intent);
        }
        KeepAlive.launchAlarm(context);
    }
}
