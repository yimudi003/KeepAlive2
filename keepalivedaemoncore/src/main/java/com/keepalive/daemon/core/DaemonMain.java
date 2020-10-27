package com.keepalive.daemon.core;

import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;

import com.keepalive.daemon.core.utils.Logger;

import java.lang.reflect.Field;

public class DaemonMain {
    IBinderManager a = new IBinderManager();
    /* access modifiers changed from: private */
    public DaemonEntity b;

    /* renamed from: c  reason: collision with root package name */
    private Parcel f4742c;
    private Parcel d;
    private Parcel e;
    private IBinder f;

    public DaemonMain(DaemonEntity daemonEntity) {
        this.b = daemonEntity;
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
            i();
            f();
            NativeKeepAlive.nativeSetSid();
            try {
                Logger.v(Logger.TAG, "setargv0 " + this.b.b);
                Process.class.getMethod("setArgV0", new Class[]{String.class}).invoke(null,
                        new Object[]{this.b.b});
            } catch (Exception e2) {
            }
            for (int i = 1; i < this.b.a.length; i++) {
                new DaemonThread(i).start();
            }
            Logger.v(Logger.TAG, this.b.b + " start lock File" + this.b.a[0]);
            NativeKeepAlive.waitFileLock(this.b.a[0]);
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
        if (this.e != null) {
            try {
                this.f.transact(this.a.c(), this.e, (Parcel) null, 1);
            } catch (Exception e2) {
                a.b(e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void c() {
        if (this.d != null) {
            try {
                this.f.transact(this.a.b(), this.d, (Parcel) null, 1);
            } catch (Exception e2) {
                a.b(e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void d() {
        if (this.f4742c != null) {
            try {
                this.f.transact(this.a.a(), this.f4742c, (Parcel) null, 1);
            } catch (Exception e2) {
                a.b(e2);
            }
        }
    }

    private void e() {
        this.f4742c = Parcel.obtain();
        this.f4742c.writeInterfaceToken("android.app.IActivityManager");
        this.f4742c.writeStrongBinder((IBinder) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.f4742c.writeInt(1);
        }
        this.b.f4740c.writeToParcel(this.f4742c, 0);
        this.f4742c.writeString((String) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.f4742c.writeInt(0);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            this.f4742c.writeString(this.b.f4740c.getComponent().getPackageName());
        }
        this.f4742c.writeInt(0);
    }

    private void f() {
        e();
        g();
        h();
    }

    private void g() {
        this.d = Parcel.obtain();
        this.d.writeInterfaceToken("android.app.IActivityManager");
        this.d.writeStrongBinder((IBinder) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.d.writeInt(1);
        }
        this.b.d.setFlags(32);
        this.b.d.writeToParcel(this.d, 0);
        this.d.writeString((String) null);
        this.d.writeStrongBinder((IBinder) null);
        this.d.writeInt(-1);
        this.d.writeString((String) null);
        this.d.writeInt(0);
        this.d.writeStringArray((String[]) null);
        this.d.writeInt(-1);
        this.d.writeInt(0);
        this.d.writeInt(0);
        this.d.writeInt(0);
        this.d.writeInt(0);
    }

    private void h() {
        this.e = Parcel.obtain();
        this.e.writeInterfaceToken("android.app.IActivityManager");
        if (Build.VERSION.SDK_INT >= 26) {
            this.e.writeInt(1);
        }
        this.b.e.getComponent().writeToParcel(this.e, 0);
        this.e.writeString((String) null);
        this.e.writeInt(0);
        this.e.writeInt(0);
        this.e.writeStrongBinder((IBinder) null);
        this.e.writeStrongBinder((IBinder) null);
        this.e.writeInt(0);
        this.e.writeString((String) null);
    }

    private void i() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object invoke = cls.getMethod("getDefault", new Class[0]).invoke(cls, new Object[0]);
            Field declaredField = invoke.getClass().getDeclaredField("mRemote");
            declaredField.setAccessible(true);
            this.f = (IBinder) declaredField.get(invoke);
            IBinder iBinder = (IBinder) Class.forName("android.os.ServiceManager").getMethod(
                    "getService", new Class[]{String.class}).invoke(null, new Object[]{"activity"});
            Logger.v(Logger.TAG, "initAmsBinder: mRemote == iBinder " + this.f);
        } catch (Throwable th) {
            a.b(th);
        }
    }

    public class DaemonThread extends Thread {
        private int b;

        public DaemonThread(int i) {
            this.b = i;
        }

        @Override
        public void run() {
            setPriority(10);
            NativeKeepAlive.waitFileLock(DaemonMain.this.b.a[this.b]);
            Logger.v(Logger.TAG, "Thread lock File finish");
            DaemonMain.this.d();
            DaemonMain.this.c();
            DaemonMain.this.b();
            Logger.v(Logger.TAG, "Thread start android finish");
            Logger.v(Logger.TAG, "Thread exit ");
        }
    }
}
