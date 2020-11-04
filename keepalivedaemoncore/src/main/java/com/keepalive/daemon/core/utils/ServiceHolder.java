package com.keepalive.daemon.core.utils;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import com.keepalive.daemon.core.IMonitorService;

import java.util.HashMap;
import java.util.Map;

public class ServiceHolder {

    private static Map<ServiceConnection, Boolean> connCache = new HashMap<>();

    private ServiceHolder() {
    }

    private static class Holder {
        private static volatile ServiceHolder INSTANCE = new ServiceHolder();
    }

    public static ServiceHolder getInstance() {
        return Holder.INSTANCE;
    }

    public static class ServiceConnectionImpl implements ServiceConnection {
        private OnServiceConnectionListener listener;
        private boolean isConnected;
        private IMonitorService monitorService;

        private ServiceConnectionImpl(OnServiceConnectionListener listener) {
            this.listener = listener;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (listener != null) {
                listener.onServiceConnection(this, false);
            }
            isConnected = false;
            connCache.put(this, false);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(Logger.TAG, "ComponentName: " + name + ", IBinder: " + service);
            monitorService = IMonitorService.Stub.asInterface(service);
            Logger.d(Logger.TAG, "IBinder asInterface: " + monitorService);
            if (listener != null && monitorService != null) {
                listener.onServiceConnection(this, true);
            }
            isConnected = true;
            connCache.put(this, true);
        }

        private boolean isConnected() {
            return isConnected;
        }

        public IMonitorService getMonitorService() {
            return monitorService;
        }
    }

    public boolean bindService(Context context, Class<? extends Service> clazz,
                               OnServiceConnectionListener listener) {
        Intent bindIntent = new Intent(context, clazz);
        bindIntent.setAction(context.getPackageName() + ".monitor.bindService");
        Logger.i(Logger.TAG, "call bindService(): " + bindIntent);
        return context.bindService(bindIntent,
                new ServiceConnectionImpl(listener),
                Context.BIND_AUTO_CREATE
        );
    }

    public void unbindService(Context context, ServiceConnection connection) {
        if (connection != null && ((ServiceConnectionImpl) connection).isConnected()) {
            try {
                Logger.i(Logger.TAG, "call unbindService(): " + connection);
                context.unbindService(connection);
                if (connCache.containsKey(connection)) {
                    connCache.remove(connection);
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public static void fireService(Context context, Class<? extends Service> clazz,
                                   boolean isForeground) {
        Intent intent = new Intent(context, clazz);
        try {
            if (isForeground) {
                ContextCompat.startForegroundService(context, new Intent(context, clazz));
            } else {
                context.startService(intent);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public interface OnServiceConnectionListener {
        void onServiceConnection(ServiceConnection connection, boolean isConnected);
    }
}
