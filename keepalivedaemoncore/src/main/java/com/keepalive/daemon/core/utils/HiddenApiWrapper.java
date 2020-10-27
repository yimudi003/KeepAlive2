package com.keepalive.daemon.core.utils;

import android.util.Log;

import java.lang.reflect.Method;

public class HiddenApiWrapper {
    private static Method sSetHiddenApiExemptions;
    private static Object sVMRuntime;

    static {
        try {
            Method forNameMethod = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethodMethod = Class.class.getDeclaredMethod(
                    "getDeclaredMethod", String.class, Class[].class);

            Class vmRuntimeClass = (Class) forNameMethod.invoke(null, "dalvik.system.VMRuntime");
            sSetHiddenApiExemptions = (Method) getDeclaredMethodMethod.invoke(vmRuntimeClass,
                    "setHiddenApiExemptions", new Class[]{String[].class});
            Method getVMRuntimeMethod = (Method) getDeclaredMethodMethod.invoke(vmRuntimeClass,
                    "getRuntime", null);
            sVMRuntime = getVMRuntimeMethod.invoke(null);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static boolean setExemptions(String... methods) {
        if ((sSetHiddenApiExemptions == null) || (sVMRuntime == null)) {
            return false;
        }

        try {
            sSetHiddenApiExemptions.invoke(sVMRuntime, new Object[]{methods});
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    public static boolean exemptAll() {
        Log.i("HiddenApiWrapper", "Start execute exemptAll method ...");
        return setExemptions("L");
    }
}
