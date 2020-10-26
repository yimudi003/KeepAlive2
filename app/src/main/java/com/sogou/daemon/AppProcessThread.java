package com.sogou.daemon;

import android.content.Context;

import com.sogou.log.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class AppProcessThread extends Thread {
    private Context context;
    private String[] b;

    /* renamed from: c  reason: collision with root package name */
    private String strArr;

    public AppProcessThread(Context context, String[] strArr, String str) {
        this.context = context;
        this.strArr = str;
        this.b = strArr;
    }

    public void run() {
        DaemonEnv env = JavaDaemon.a().env();
        DaemonEntity daemonEntity = new DaemonEntity();
        daemonEntity.b = this.strArr;
        daemonEntity.a = this.b;
        daemonEntity.f4740c = env.d;
        daemonEntity.d = env.e;
        daemonEntity.e = env.f;
        String str = env.publicSourceDir;
        ArrayList arrayList = new ArrayList();
        arrayList.add("export CLASSPATH=$CLASSPATH:" + str);
        String str2 = env.f4741c;
        String str3 = "export _LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + str2;
        arrayList.add(str3);
        arrayList.add("export LD_LIBRARY_PATH=/system/lib/:/vendor/lib/:" + str2);
        arrayList.add(String.format("%s / %s %s --application --nice-name=%s &",
                new Object[]{new File("/system/bin/app_process32").exists() ?
                        "app_process32" : "app_process", DaemonMain.class.getName(),
                        daemonEntity.toString(), this.strArr}));
        File file = new File("/");
        String[] strArr = new String[arrayList.size()];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = (String) arrayList.get(i);
            Log.v(Log.TAG, strArr[i]);
        }
        ShellExecutor.a(file, (Map) null, strArr);
    }
}
