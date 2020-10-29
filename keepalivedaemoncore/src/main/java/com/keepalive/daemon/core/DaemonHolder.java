package com.keepalive.daemon.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.keepalive.daemon.core.activity.ScreenManager;
import com.keepalive.daemon.core.component.DaemonInstrumentation;
import com.keepalive.daemon.core.component.DaemonReceiver;
import com.keepalive.daemon.core.component.DaemonService;
import com.keepalive.daemon.core.utils.HiddenApiWrapper;
import com.keepalive.daemon.core.utils.Logger;

public class DaemonHolder {

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiWrapper.exemptAll();
        }
    }

    private DaemonHolder() {
    }

    private static class Holder {
        private static volatile DaemonHolder INSTANCE = new DaemonHolder();
    }

    public static DaemonHolder getInstance() {
        return Holder.INSTANCE;
    }

    public void attach(Context base) {
        JavaDaemon.getInstance().fire(
                base,
                new Intent(base, DaemonService.class),
                new Intent(base, DaemonReceiver.class),
                new Intent(base, DaemonInstrumentation.class)
        );
        ScreenManager.getInstance().startActivity(base);

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
