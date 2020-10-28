package com.keepalive.daemon.core;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;

import com.keepalive.daemon.core.utils.Logger;

import java.lang.reflect.Field;

public class DaemonMain {
    IBinderManager a = new IBinderManager();
    public DaemonEntity b;

    private Parcel p1;
    private Parcel d;
    private Parcel e;
    private IBinder f;

    public DaemonMain(DaemonEntity daemonEntity) {
        b = daemonEntity;
    }

    public static void main(String[] strArr) {
        DaemonEntity a2 = DaemonEntity.create(strArr[0]);
        if (a2 != null) {
            new DaemonMain(a2).execute();
        }
        Process.killProcess(Process.myPid());
    }

    private void execute() {
        try {
            initAmsBinder();
            f();
            NativeKeepAlive.nativeSetSid();
            try {
                Logger.v(Logger.TAG, "setArgV0 " + b.b);
                Process.class.getMethod("setArgV0", new Class[]{String.class}).invoke(null,
                        new Object[]{b.b});
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 1; i < b.a.length; i++) {
                new DaemonThread(this, i).start();
            }
            Logger.v(Logger.TAG, b.b + " start lock File" + b.a[0]);
            NativeKeepAlive.waitFileLock(b.a[0]);
            Logger.v(Logger.TAG, "lock File finish");
            d();
            c();
            b();
            Logger.v(Logger.TAG, "start android finish");
        } catch (Throwable th) {
            a.b(th);
        }
    }

    /* access modifiers changed from: private */
    public void b() {
        if (e != null) {
            try {
                f.transact(a.c(), e, null, 1);
            } catch (Exception e2) {
                a.b(e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void c() {
        if (d != null) {
            try {
                f.transact(a.b(), d, null, 1);
            } catch (Exception e2) {
                a.b(e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void d() {
        if (p1 != null) {
            try {
                f.transact(a.a(), p1, null, 1);
            } catch (Exception e2) {
                a.b(e2);
            }
        }
    }

    private void e() {
        p1 = Parcel.obtain();
        p1.writeInterfaceToken("android.app.IActivityManager");
        p1.writeStrongBinder(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p1.writeInt(1);
        }
        b.intent.writeToParcel(p1, 0);
        p1.writeString(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p1.writeInt(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            p1.writeString(b.intent.getComponent().getPackageName());
        }
        p1.writeInt(0);
    }

    private void f() {
        e();
        g();
        h();
    }

    @SuppressLint("WrongConstant")
    private void g() {
        d = Parcel.obtain();
        d.writeInterfaceToken("android.app.IActivityManager");
        d.writeStrongBinder(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            d.writeInt(1);
        }
        b.d.setFlags(32);
        b.d.writeToParcel(d, 0);
        d.writeString(null);
        d.writeStrongBinder(null);
        d.writeInt(-1);
        d.writeString(null);
        d.writeInt(0);
        d.writeStringArray(null);
        d.writeInt(-1);
        d.writeInt(0);
        d.writeInt(0);
        d.writeInt(0);
        d.writeInt(0);
    }

    private void h() {
        e = Parcel.obtain();
        e.writeInterfaceToken("android.app.IActivityManager");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            e.writeInt(1);
        }
        b.e.getComponent().writeToParcel(e, 0);
        e.writeString(null);
        e.writeInt(0);
        e.writeInt(0);
        e.writeStrongBinder(null);
        e.writeStrongBinder(null);
        e.writeInt(0);
        e.writeString(null);
    }

    private void initAmsBinder() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object invoke = cls.getMethod("getDefault", new Class[0]).invoke(cls, new Object[0]);
            Field declaredField = invoke.getClass().getDeclaredField("mRemote");
            declaredField.setAccessible(true);
            f = (IBinder) declaredField.get(invoke);
//            IBinder iBinder = (IBinder) Class.forName("android.os.ServiceManager").getMethod(
//                    "getService", new Class[]{String.class}).invoke(null, new Object[]{"activity"});
            Logger.v(Logger.TAG, "initAmsBinder: mRemote == iBinder " + f);
        } catch (Throwable th) {
            a.b(th);
        }
    }

    static class DaemonThread extends Thread {
        private DaemonMain thiz;
        private int index;

        public DaemonThread(DaemonMain thiz, int index) {
            this.thiz = thiz;
            this.index = index;
        }

        @Override
        public void run() {
            setPriority(10);
            NativeKeepAlive.waitFileLock(thiz.b.a[index]);
            Logger.v(Logger.TAG, "Thread lock File finish");
            thiz.d();
            thiz.c();
            thiz.b();
            Logger.v(Logger.TAG, "Thread start android finish");
        }
    }
}
