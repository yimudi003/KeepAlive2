package com.keepalive.daemon.core.utils;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.keepalive.daemon.core.IMonitorService;

public class ServiceHolder {

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
        return context.bindService(bindIntent,
                new ServiceConnectionImpl(listener),
                Context.BIND_AUTO_CREATE
        );
    }

    public void unbindService(Context context, ServiceConnectionImpl connection) {
        if (connection != null && connection.isConnected()) {
            try {
                context.unbindService(connection);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public interface OnServiceConnectionListener {
        void onServiceConnection(ServiceConnection connection, boolean isConnected);
    }
}
