package com.keepalive.daemon.core.component;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.keepalive.daemon.core.IMonitorService;
import com.keepalive.daemon.core.R;
import com.keepalive.daemon.core.utils.Logger;

public abstract class DaemonBaseService extends Service {

    private IMonitorService.Stub binder = new IMonitorService.Stub() {
        @Override
        public void processMessage(Bundle bundle) throws RemoteException {
            DaemonBaseService.this.processMessage(bundle);
        }
    };

    private void processMessage(Bundle bundle) {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Logger.TAG)
                    .setContentTitle("Title")
                    .setContentText("Text")
                    .setSmallIcon(R.drawable.ic_launcher);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManagerCompat
                        .from(this)
                        .createNotificationChannel(new NotificationChannel(
                                Logger.TAG,
                                Logger.TAG,
                                NotificationManager.IMPORTANCE_LOW));
            }
            startForeground(9999, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(Logger.TAG, "############### intent: " + intent + ", startId: " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
