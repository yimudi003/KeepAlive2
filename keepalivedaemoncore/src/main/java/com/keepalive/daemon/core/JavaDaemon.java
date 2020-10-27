package com.keepalive.daemon.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import com.keepalive.daemon.core.utils.Logger;
import com.keepalive.daemon.core.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class JavaDaemon {
    private static final String COLON_SEPARATOR = ":";
    private DaemonEnv env;

    private JavaDaemon() {
    }

    private static class Holder {
        private static volatile JavaDaemon INSTANCE = new JavaDaemon();
    }

    public static JavaDaemon getInstance() {
        return Holder.INSTANCE;
    }

    public DaemonEnv env() {
        return env;
    }

    public void fire(Context context, Intent intent, Intent intent2, Intent intent3) {
        Logger.i(Logger.TAG, "################################################### fire!!!");
        env = new DaemonEnv();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        env.publicSourceDir = applicationInfo.publicSourceDir;
        env.nativeLibraryDir = applicationInfo.nativeLibraryDir;
        env.intent = intent;
        env.intent2 = intent2;
        env.intent3 = intent3;
        env.processName = Utils.getProcessName();
        Logger.v(Logger.TAG, "new DaemonEnv: " + env);
    }

    public void fire(Context context, String[] strArr) {
        boolean z;
        String processName = Utils.getProcessName();
        Logger.v(Logger.TAG, "processName : " + processName);
        if (processName.startsWith(context.getPackageName()) && processName.contains(COLON_SEPARATOR)) {
            String substring = processName.substring(processName.lastIndexOf(COLON_SEPARATOR) + 1);
            List arrayList = new ArrayList();
            if (strArr != null) {
                z = false;
                for (String str : strArr) {
                    if (str.equals(substring)) {
                        z = true;
                    } else {
                        arrayList.add(str);
                    }
                }
            } else {
                z = false;
            }
            if (z) {
                Logger.v(Logger.TAG, "app lock file start: " + substring);
                NativeKeepAlive.lockFile(context.getFilesDir() + "/" + substring + "_daemon");
                Logger.v(Logger.TAG, "app lock file finish");
                String[] strArr2 = new String[arrayList.size()];
                for (int i = 0; i < strArr2.length; i++) {
                    strArr2[i] = context.getFilesDir() + "/" + arrayList.get(i) + "_daemon";
                }
                new AppProcessThread(context, strArr2, "daemon").start();
            }
        }
    }
}
