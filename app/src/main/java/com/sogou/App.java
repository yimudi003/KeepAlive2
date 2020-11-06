package com.sogou;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.keepalive.daemon.core.Constants;
import com.keepalive.daemon.core.DaemonHolder;
import com.keepalive.daemon.core.utils.Logger;
import com.sogou.daemon.R;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Logger.v(Logger.TAG, "attachBaseContext");
        DaemonHolder.getInstance().attach(base, this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Intent intent = new Intent(this, MyService.class);
            intent.putExtra(Constants.NOTIFICATION_ICON, R.mipmap.ic_launcher_round);
            intent.putExtra(Constants.NOTIFICATION_TITLE, getApplicationInfo().loadLabel(getPackageManager()));
            intent.putExtra(Constants.NOTIFICATION_TEXT, "Hello, world!");
            intent.putExtra(Constants.NOTIFICATION_ACTIVITY, MainActivity.class.getCanonicalName());
            ContextCompat.startForegroundService(this, intent);
        } catch (Throwable th) {
            Logger.e(Logger.TAG, "failed to start foreground service: " + th.getMessage());
        }
    }
}
