package com.keepalive.daemon.core;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;

import com.keepalive.daemon.core.scheduler.FutureScheduler;
import com.keepalive.daemon.core.scheduler.SingleThreadFutureScheduler;
import com.keepalive.daemon.core.utils.Logger;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Arrays;

public class DaemonMain {
    private IBinderManager binderManager = new IBinderManager();
    public DaemonEntity entity;

    private Parcel p;
    private Parcel p2;
    private Parcel p3;
    private IBinder binder;

    private static volatile FutureScheduler futureScheduler;

    private DaemonMain(DaemonEntity entity) {
        this.entity = entity;
    }

    public static void main(String[] strArr) {
        Logger.d(Logger.TAG, "call main(): " + Arrays.toString(strArr));
        if (futureScheduler == null) {
            synchronized (DaemonMain.class) {
                if (futureScheduler == null) {
                    futureScheduler = new SingleThreadFutureScheduler(
                            "daemonmain-holder",
                            true
                    );
                }
            }
        }

        DaemonEntity entity = DaemonEntity.create(strArr[0]);
        if (entity != null) {
            new DaemonMain(entity).execute();
        }
        Process.killProcess(Process.myPid());
    }

    private void execute() {
        try {
            initAmsBinder();
            assembleParcel();
            NativeKeepAlive.nativeSetSid();
            try {
                Logger.v(Logger.TAG, "setArgV0: " + entity.str);
                Process.class.getMethod("setArgV0", new Class[]{String.class}).invoke(null,
                        new Object[]{entity.str});
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 1; i < entity.strArr.length; i++) {
                futureScheduler.scheduleFuture(new DaemonRunnable(this, i), 0);
            }
            Logger.v(Logger.TAG, "[" + entity.str + "] wait file lock start: " + entity.strArr[0]);
            NativeKeepAlive.waitFileLock(entity.strArr[0]);
            Logger.v(Logger.TAG, "[" + entity.str + "] wait file lock finish");
            startService();
            broadcastIntent();
            startInstrumentation();
            Logger.v(Logger.TAG, "[" + entity.str + "] start android finish");
        } catch (Throwable th) {
            binderManager.thrown(th);
        }
    }

    public void startInstrumentation() {
        Logger.i(Logger.TAG, "call startInstrumentation(): " + p3);
        if (p3 != null) {
            try {
                binder.transact(binderManager.startInstrumentation(), p3, null, 1);
            } catch (Throwable th) {
                binderManager.thrown(th);
            }
        }
    }

    public void broadcastIntent() {
        Logger.i(Logger.TAG, "call broadcastIntent(): " + p2);
        if (p2 != null) {
            try {
                binder.transact(binderManager.broadcastIntent(), p2, null, 1);
            } catch (Throwable th) {
                binderManager.thrown(th);
            }
        }
    }

    public void startService() {
        Logger.i(Logger.TAG, "call startService(): " + p);
        if (p != null) {
            try {
                binder.transact(binderManager.startService(), p, null, 1);
            } catch (Throwable th) {
                binderManager.thrown(th);
            }
        }
    }

    private void assembleParcel() {
        assembleServiceParcel();
        assembleBroadcastParcel();
        assembleInstrumentationParcel();
    }

    private void assembleServiceParcel() {
        p = Parcel.obtain();
        p.writeInterfaceToken("android.app.IActivityManager");
        p.writeStrongBinder(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p.writeInt(1);
        }
        entity.intent.writeToParcel(p, 0);
        p.writeString(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p.writeInt(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            p.writeString(entity.intent.getComponent().getPackageName());
        }
        p.writeInt(0);
    }

    @SuppressLint("WrongConstant")
    private void assembleBroadcastParcel() {
        p2 = Parcel.obtain();
        p2.writeInterfaceToken("android.app.IActivityManager");
        p2.writeStrongBinder(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p2.writeInt(1);
        }
        entity.intent2.setFlags(32);
        entity.intent2.writeToParcel(p2, 0);
        p2.writeString(null);
        p2.writeStrongBinder(null);
        p2.writeInt(-1);
        p2.writeString(null);
        p2.writeInt(0);
        p2.writeStringArray(null);
        p2.writeInt(-1);
        p2.writeInt(0);
        p2.writeInt(0);
        p2.writeInt(0);
        p2.writeInt(0);
    }

    private void assembleInstrumentationParcel() {
        p3 = Parcel.obtain();
        p3.writeInterfaceToken("android.app.IActivityManager");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            p3.writeInt(1);
        }
        entity.intent3.getComponent().writeToParcel(p3, 0);
        p3.writeString(null);
        p3.writeInt(0);
        p3.writeInt(0);
        p3.writeStrongBinder(null);
        p3.writeStrongBinder(null);
        p3.writeInt(0);
        p3.writeString(null);
    }

    private void initAmsBinder() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object invoke = cls.getMethod("getDefault", new Class[0]).invoke(cls, new Object[0]);
            Field field = invoke.getClass().getDeclaredField("mRemote");
            field.setAccessible(true);
            binder = (IBinder) field.get(invoke);
            field.setAccessible(false);
            Logger.v(Logger.TAG, "initAmsBinder: mRemote == iBinder " + binder);
        } catch (Throwable th) {
            binderManager.thrown(th);
        }

        if (binder == null) {
            try {
                binder = (IBinder) Class.forName("android.os.ServiceManager").getMethod(
                        "getService", new Class[]{String.class}).invoke(null,
                        new Object[]{"activity"});
            } catch (Throwable th) {
                binderManager.thrown(th);
            }
        }
    }

    class DaemonRunnable implements Runnable {
        private WeakReference<DaemonMain> thiz;
        private int index;

        private DaemonRunnable(DaemonMain thiz, int index) {
            this.thiz = new WeakReference<>(thiz);
            this.index = index;
        }

        @Override
        public void run() {
            Logger.v(Logger.TAG, "[Thread] wait file lock start: " + index);
            NativeKeepAlive.waitFileLock(thiz.get().entity.strArr[index]);
            Logger.v(Logger.TAG, "[Thread] wait file lock finished");
            thiz.get().startService();
            thiz.get().broadcastIntent();
            thiz.get().startInstrumentation();
            Logger.v(Logger.TAG, "[Thread] start android finish");
        }
    }
}
