package com.keepalive.daemon.core;

import android.content.Context;

import com.keepalive.daemon.core.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppProcessThread extends Thread {
    private Context context;
    private String[] b;

    private String strArr;

    public AppProcessThread(Context context, String[] strArr, String str) {
        this.context = context;
        this.strArr = str;
        this.b = strArr;
    }

    @Override
    public void run() {
        DaemonEnv env = JavaDaemon.getInstance().env();
        DaemonEntity daemonEntity = new DaemonEntity();
        daemonEntity.b = this.strArr;
        daemonEntity.a = this.b;
        daemonEntity.f4740c = env.intent;
        daemonEntity.d = env.intent2;
        daemonEntity.e = env.intent3;
        List arrayList = new ArrayList();
        arrayList.add("export CLASSPATH=$CLASSPATH:" + env.publicSourceDir);
        if (env.nativeLibraryDir.contains("arm64")) {
            arrayList.add("export _LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + env.nativeLibraryDir);
            arrayList.add("export LD_LIBRARY_PATH=/system/lib64/:/vendor/lib64/:" + env.nativeLibraryDir);
            arrayList.add(String.format("%s / %s %s --application --nice-name=%s &",
                    new Object[]{new File("/system/bin/app_process").exists() ?
                            "app_process" : "app_process", DaemonMain.class.getName(),
                            daemonEntity.toString(), this.strArr}));
        } else {
            arrayList.add("export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + env.nativeLibraryDir);
            arrayList.add("export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + env.nativeLibraryDir);
            arrayList.add(String.format("%s / %s %s --application --nice-name=%s &",
                    new Object[]{new File("/system/bin/app_process32").exists() ?
                            "app_process32" : "app_process", DaemonMain.class.getName(),
                            daemonEntity.toString(), this.strArr}));
        }
        File file = new File("/");
        String[] strArr = new String[arrayList.size()];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = (String) arrayList.get(i);
            Logger.v(Logger.TAG, strArr[i]);
        }
        ShellExecutor.execute(file, null, strArr);
    }
}
