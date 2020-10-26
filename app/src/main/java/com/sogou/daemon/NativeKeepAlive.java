package com.sogou.daemon;

public class NativeKeepAlive {
    public static native void lockFile(String str);

    public static native void nativeSetSid();

    public static native void waitFileLock(String str);

    static {
        try {
            System.loadLibrary(/*"core_alive"*/"daemon_core");
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
