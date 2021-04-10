package com.keepalive.daemon.core.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.ServiceHolder;

public class DaemonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.v(Logger.TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! receiver: "
                + intent);
        ServiceHolder.fireService(context, DaemonService.class, false);
    }
}
