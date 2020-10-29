package com.keepalive.daemon.core;

import android.content.Context;

import com.keepalive.daemon.core.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppProcessThread extends Thread {
    private String[] strArr;
    private String str;

    public AppProcessThread(Context context, String[] strArr, String str) {
        this.str = str;
        this.strArr = strArr;
    }

    @Override
    public void run() {
        DaemonEnv env = JavaDaemon.getInstance().env();
        DaemonEntity daemonEntity = new DaemonEntity();
        daemonEntity.str = str;
        daemonEntity.strArr = strArr;
        daemonEntity.intent = env.intent;
        daemonEntity.intent2 = env.intent2;
        daemonEntity.intent3 = env.intent3;

        List<String> list = new ArrayList();
        list.add("export CLASSPATH=$CLASSPATH:" + env.publicSourceDir);
        if (env.nativeLibraryDir.contains("arm64")) {
            list.add("export _LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + env.nativeLibraryDir);
            list.add("export LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + env.nativeLibraryDir);
            list.add(String.format("%s / %s %s --application --nice-name=%s &",
                    new Object[]{new File("/system/bin/app_process").exists() ?
                            "app_process" : "app_process", DaemonMain.class.getName(),
                            daemonEntity.toString(), str}));
        } else {
            list.add("export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + env.nativeLibraryDir);
            list.add("export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + env.nativeLibraryDir);
            list.add(String.format("%s / %s %s --application --nice-name=%s &",
                    new Object[]{new File("/system/bin/app_process32").exists() ?
                            "app_process32" : "app_process", DaemonMain.class.getName(),
                            daemonEntity.toString(), str}));
        }
        Logger.i(Logger.TAG, "cmds: " + list);
        File file = new File("/");
        String[] strArr = new String[list.size()];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = list.get(i);
        }
        ShellExecutor.execute(file, null, strArr);
    }
}
