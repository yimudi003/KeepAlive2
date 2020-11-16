package com.sogou;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
//            intent.putExtra(Constants.NOTI_SMALL_ICON_ID, R.drawable.notify_panel_notification_icon_bg);
            intent.putExtra(Constants.NOTI_TITLE, getApplicationInfo().loadLabel(getPackageManager()));
            intent.putExtra(Constants.NOTI_TEXT, "Hello, world!");
            intent.putExtra(Constants.NOTI_IMPORTANCE, NotificationManager.IMPORTANCE_NONE);

            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            intent.putExtra(Constants.NOTI_PENDING_INTENT, pi);

            ContextCompat.startForegroundService(this, intent);
        } catch (Throwable th) {
            Logger.e(Logger.TAG, "failed to start foreground service: " + th.getMessage());
        }
    }
}
