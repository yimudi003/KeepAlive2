package com.keepalive.daemon.core;

public class NativeKeepAlive {
    public static native void lockFile(String str);

    public static native void nativeSetSid();

    public static native void waitFileLock(String str);

    public native void doDaemon(String indicatorSelfPath,
                                String indicatorDaemonPath,
                                String observerSelfPath,
                                String observerDaemonPath,
                                String packageName,
                                String serviceName,
                                int sdkVersion);

    public native void test(String packageName, String serviceName, int sdkVersion);

    static {
        try {
            System.loadLibrary("daemon_core");
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
