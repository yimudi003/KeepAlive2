package com.keepalive.daemon.core;

import java.lang.reflect.Field;

public class IBinderManager {
    private int startService = invoke("TRANSACTION_startService",
            "START_SERVICE_TRANSACTION");
    private int broadcastIntent = invoke("TRANSACTION_broadcastIntent",
            "BROADCAST_INTENT_TRANSACTION");
    private int startInstrumentation = invoke("TRANSACTION_startInstrumentation",
            "START_INSTRUMENTATION_TRANSACTION");

    public int invoke(String str, String str2) {
        try {
            Class<?> cls = Class.forName("android.app.IActivityManager$Stub");
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.getInt(cls);
        } catch (Exception unused) {
            try {
                Class<?> cls2 = Class.forName("android.app.IActivityManager");
                Field declaredField2 = cls2.getDeclaredField(str2);
                declaredField2.setAccessible(true);
                return declaredField2.getInt(cls2);
            } catch (Exception unused2) {
                return -1;
            }
        }
    }

    public int startService() {
        return startService;
    }

    public int broadcastIntent() {
        return broadcastIntent;
    }

    public int startInstrumentation() {
        return startInstrumentation;
    }

    public void thrown(Throwable th) {
        th.printStackTrace();
    }
}
