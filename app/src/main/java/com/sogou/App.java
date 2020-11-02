package com.sogou;

import android.app.Application;
import android.content.Context;

import com.keepalive.daemon.core.DaemonHolder;
import com.keepalive.daemon.core.utils.Logger;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Logger.v(Logger.TAG, "attachBaseContext");
        DaemonHolder.getInstance().attach(base, this);
    }
}
