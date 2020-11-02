package com.keepalive.daemon.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import com.keepalive.daemon.core.component.DaemonInstrumentation;
import com.keepalive.daemon.core.component.DaemonReceiver;
import com.keepalive.daemon.core.component.DaemonService;
import com.keepalive.daemon.core.utils.HiddenApiWrapper;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.ServiceHolder;

import java.util.HashMap;
import java.util.Map;

public class DaemonHolder {

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiWrapper.exemptAll();
        }
    }

    private static Map<Activity, ServiceConnection> connCache = new HashMap<>();

    private DaemonHolder() {
    }

    private static class Holder {
        private static volatile DaemonHolder INSTANCE = new DaemonHolder();
    }

    public static DaemonHolder getInstance() {
        return Holder.INSTANCE;
    }

    public void attach(Context base, Application app) {
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
                Logger.v(Logger.TAG, String.format("====> [%s] created", activity.getLocalClassName()));
                ServiceHolder.getInstance().bindService(activity, DaemonService.class,
                        new ServiceHolder.OnServiceConnectionListener() {
                            @Override
                            public void onServiceConnection(ServiceConnection connection, boolean isConnected) {
                                if (isConnected) {
                                    connCache.put(activity, connection);
                                }
                            }
                        });
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Logger.v(Logger.TAG, String.format("====> [%s] started", activity.getLocalClassName()));
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Logger.v(Logger.TAG, String.format("====> [%s] resumed", activity.getLocalClassName()));
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Logger.v(Logger.TAG, String.format("====> [%s] paused", activity.getLocalClassName()));
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Logger.v(Logger.TAG, String.format("====> [%s] stopped", activity.getLocalClassName()));
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Logger.v(Logger.TAG, String.format("====> [%s] save instance state", activity.getLocalClassName()));
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Logger.v(Logger.TAG, String.format("====> [%s] destroyed", activity.getLocalClassName()));
                if (connCache.containsKey(activity)) {
                    ServiceHolder.getInstance().unbindService(activity, connCache.get(activity));
                }
            }
        });

        JavaDaemon.getInstance().fire(
                base,
                new Intent(base, DaemonService.class),
                new Intent(base, DaemonReceiver.class),
                new Intent(base, DaemonInstrumentation.class)
        );

//        KeepAliveConfigs configs = new KeepAliveConfigs(
//                new KeepAliveConfigs.Config(base.getPackageName() + ":daemon",
//                        DaemonService.class.getCanonicalName()));
////        configs.ignoreBatteryOptimization();
////        configs.rebootThreshold(10 * 1000, 3);
//        configs.setOnBootReceivedListener(new KeepAliveConfigs.OnBootReceivedListener() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Logger.d(Logger.TAG, "############################# onReceive(): intent=" + intent);
//                context.startService(new Intent(context, DaemonService.class));
//                ScreenManager.getInstance().startActivity(context);
//            }
//        });
//        KeepAlive.init(base, configs);
    }
}
