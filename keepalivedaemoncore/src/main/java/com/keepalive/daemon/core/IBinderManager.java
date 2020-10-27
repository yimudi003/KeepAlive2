package com.keepalive.daemon.core;

import java.lang.reflect.Field;

public class IBinderManager {
    private int a = a("TRANSACTION_startService", "START_SERVICE_TRANSACTION");
    private int b = a("TRANSACTION_broadcastIntent", "BROADCAST_INTENT_TRANSACTION");

    /* renamed from: c  reason: collision with root package name */
    private int f5177c = a("TRANSACTION_startInstrumentation", "START_INSTRUMENTATION_TRANSACTION");

    public int a(String str, String str2) {
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

    public int a() {
        return this.a;
    }

    public int b() {
        return this.b;
    }

    public int c() {
        return this.f5177c;
    }

    public void b(Throwable th) {
        th.printStackTrace();
    }
}
