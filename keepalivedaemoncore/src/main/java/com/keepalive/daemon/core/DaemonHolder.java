package com.keepalive.daemon.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.keepalive.daemon.core.component.DaemonInstrumentation;
import com.keepalive.daemon.core.component.DaemonReceiver;
import com.keepalive.daemon.core.component.DaemonService;
import com.keepalive.daemon.core.service.DaemonService2;
import com.keepalive.daemon.core.utils.HiddenApiWrapper;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.RuntimeUtil;
import com.keepalive.daemon.core.utils.Utils;

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
        if (false) {
            RuntimeUtil.is64Bit();
            Utils.getCurrSOLoaded();
        }

        JavaDaemon.getInstance().fire(
                base,
                new Intent(base, DaemonService.class),
                new Intent(base, DaemonReceiver.class),
                new Intent(base, DaemonInstrumentation.class)
        );
        String[] strArr = {"daemon", "assist1", "assist2"};
        JavaDaemon.getInstance().fire(base, strArr);

        KeepAliveConfigs configs = new KeepAliveConfigs(
                new KeepAliveConfigs.Config(base.getPackageName() + ":resident2",
                        DaemonService2.class.getCanonicalName()));
//        configs.ignoreBatteryOptimization();
//        configs.rebootThreshold(10 * 1000, 3);
        configs.setOnBootReceivedListener(new KeepAliveConfigs.OnBootReceivedListener() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Logger.d(Logger.TAG, "############################# onReceive(): intent=" + intent);
                // 设置服务自启
                context.startService(new Intent(context, DaemonService2.class));
            }
        });
        KeepAlive.init(base, configs);
    }
}
