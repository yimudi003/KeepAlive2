package com.keepalive.daemon.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import com.keepalive.daemon.core.component.DaemonService;
import com.keepalive.daemon.core.scheduler.FutureScheduler;
import com.keepalive.daemon.core.scheduler.SingleThreadFutureScheduler;
import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.ServiceHolder;
import com.keepalive.daemon.core.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.keepalive.daemon.core.Constants.COLON_SEPARATOR;

public class JavaDaemon {
    private volatile static FutureScheduler scheduler;

    private JavaDaemon() {
        if (scheduler == null) {
            synchronized (JavaDaemon.class) {
                if (scheduler == null) {
                    scheduler = new SingleThreadFutureScheduler("javadaemon-holder", true);
                }
            }
        }
    }

    private static class Holder {
        private volatile static JavaDaemon INSTANCE = new JavaDaemon();
    }

    public static JavaDaemon getInstance() {
        return Holder.INSTANCE;
    }

    public void fire(Context context, Intent intent, Intent intent2, Intent intent3) {
        DaemonEnv env = new DaemonEnv();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        env.publicSourceDir = applicationInfo.publicSourceDir;
        env.nativeLibraryDir = applicationInfo.nativeLibraryDir;
        env.intent = intent;
        env.intent2 = intent2;
        env.intent3 = intent3;
        env.processName = Utils.getProcessName();

        String[] args = {"daemon", "assist1", "assist2"};
        fire(context, env, args);
    }

    private void fire(Context context, DaemonEnv env, String[] args) {
        Logger.i(Logger.TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! fire(): "
                + "env=" + env + ", args=" + Arrays.toString(args));
        boolean hit = false;
        String processName = env.processName;
        if (processName.startsWith(context.getPackageName()) && processName.contains(COLON_SEPARATOR)) {
            String niceName = processName.substring(processName.lastIndexOf(COLON_SEPARATOR) + 1);
            List<String> list = new ArrayList();
            for (String arg : args) {
                if (arg.equals(niceName)) {
                    hit = true;
                } else {
                    list.add(arg);
                }
            }
            if (hit) {
                Logger.v(Logger.TAG, "app lock file start: " + niceName);
                NativeKeepAlive.lockFile(context.getFilesDir() + "/" + niceName + "_d");
                Logger.v(Logger.TAG, "app lock file finish");
                String[] strArr = new String[list.size()];
                for (int i = 0; i < strArr.length; i++) {
                    strArr[i] = context.getFilesDir() + "/" + list.get(i) + "_d";
                }
                scheduler.scheduleFuture(new AppProcessRunnable(env, strArr, niceName), 0);
            }
        } else if (processName.equals(context.getPackageName())) {
            ServiceHolder.fireService(context, DaemonService.class, false);
        }
    }
}
