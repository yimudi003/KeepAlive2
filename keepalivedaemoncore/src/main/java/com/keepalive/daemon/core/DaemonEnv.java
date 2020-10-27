package com.keepalive.daemon.core;

import android.content.Intent;

public class DaemonEnv {
    public String processName;
    public String publicSourceDir;

    /* renamed from: c  reason: collision with root package name */
    public String nativeLibraryDir;
    public Intent intent;
    public Intent intent2;
    public Intent intent3;

    @Override
    public String toString() {
        return "DaemonEnv{" +
                "processName='" + processName + '\'' +
                ", publicSourceDir='" + publicSourceDir + '\'' +
                ", nativeLibraryDir='" + nativeLibraryDir + '\'' +
                ", intent=" + intent +
                ", intent2=" + intent2 +
                ", intent3=" + intent3 +
                '}';
    }
}
