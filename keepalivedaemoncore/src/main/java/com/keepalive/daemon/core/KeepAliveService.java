package com.keepalive.daemon.core;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.keepalive.daemon.core.component.DaemonBaseService;
import com.keepalive.daemon.core.utils.Logger;

public class KeepAliveService extends DaemonBaseService {

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bindDaemonService();
    }

    private IBinder binder;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = iBinder;
            Logger.d(Logger.TAG, "++++++++++++++++++++++++++++++++++++++++++++ " + iBinder);
            try {
                iBinder.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (binder != null) {
                binder.unlinkToDeath(this, 0);
                binder = null;
            }
            bindDaemonService();
        }
    };

    protected void bindDaemonService() {
        if (KeepAlive.client != null && KeepAlive.client.config != null) {
            String processName = KeepAlive.getProcessName();
            if (processName == null) {
                return;
            }

            KeepAliveConfigs configs = KeepAlive.client.config;
            if (processName.startsWith(configs.PERSISTENT_CONFIG.processName)) {
                Intent intent = new Intent();
                ComponentName component = new ComponentName(getPackageName(),
                        configs.DAEMON_ASSISTANT_CONFIG.serviceName);
                intent.setComponent(component);
                bindService(intent, conn, BIND_AUTO_CREATE);
            } else if (processName.startsWith(configs.DAEMON_ASSISTANT_CONFIG.processName)) {
                Intent intent = new Intent();
                ComponentName component = new ComponentName(getPackageName(),
                        configs.PERSISTENT_CONFIG.serviceName);
                intent.setComponent(component);
                bindService(intent, conn, BIND_AUTO_CREATE);
            }
        }
    }
}
