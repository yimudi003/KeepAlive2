package com.sogou.daemon.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sogou.daemon.utils.Utils;
import com.sogou.log.Log;

public class DaemonReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.v(Log.TAG, "onReceiver");
    }
}
