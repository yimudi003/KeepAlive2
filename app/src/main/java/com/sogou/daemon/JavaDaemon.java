package com.sogou.daemon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import com.sogou.daemon.utils.Utils;
import com.sogou.log.Log;

import java.util.ArrayList;

public class JavaDaemon {
    private static final String COLON_SEPARATOR = ":";
    private static JavaDaemon a = new JavaDaemon();
    private DaemonEnv env;

    public static JavaDaemon a() {
        return a;
    }

    public DaemonEnv env() {
        return this.env;
    }

    public void a(Context context, Intent intent, Intent intent2, Intent intent3) {
        this.env = new DaemonEnv();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        this.env.publicSourceDir = applicationInfo.publicSourceDir;
        this.env.f4741c = applicationInfo.nativeLibraryDir;
        this.env.d = intent;
        this.env.e = intent2;
        this.env.f = intent3;
        this.env.a = Utils.getProcessName();
    }

    public void a(Context context, String[] strArr) {
        boolean z;
        String a2 = Utils.getProcessName();
        Log.v(Log.TAG, "a2 : " + a2);
        if (a2.startsWith(context.getPackageName()) && a2.contains(COLON_SEPARATOR)) {
            String substring = a2.substring(a2.lastIndexOf(COLON_SEPARATOR) + 1);
            ArrayList arrayList = new ArrayList();
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
                Log.v(Log.TAG, "app lock file start : " + substring);
                NativeKeepAlive.lockFile(context.getFilesDir() + "/" + substring + "_daemon");
                Log.v(Log.TAG, "app lock file finish");
                String[] strArr2 = new String[arrayList.size()];
                for (int i = 0; i < strArr2.length; i++) {
                    strArr2[i] = context.getFilesDir() + "/" + ((String) arrayList.get(i)) + "_daemon";
                }
                new AppProcessThread(context, strArr2, "daemon").start();
            }
        }
    }
}
