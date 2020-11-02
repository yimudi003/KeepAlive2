package com.keepalive.daemon.core.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.keepalive.daemon.core.utils.Logger;

public class DaemonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.v(Logger.TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! receiver: "
                + intent);
        ContextCompat.startForegroundService(context, new Intent(context, DaemonService.class));
    }
}
