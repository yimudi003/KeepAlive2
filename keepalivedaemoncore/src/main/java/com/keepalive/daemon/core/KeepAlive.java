package com.keepalive.daemon.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.keepalive.daemon.core.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class KeepAlive {

    KeepAliveConfigs config;

    protected volatile static KeepAlive client;

    private KeepAlive(KeepAliveConfigs config) {
        this.config = config;
    }

    public static void init(Context base, KeepAliveConfigs config) {
        if (client == null) {
            synchronized (KeepAlive.class) {
                if (client == null) {
                    client = new KeepAlive(config);
                    client.initDaemon(base);
                }
            }
        }
    }

    private static final String DAEMON_PERMITTING_SP_FILENAME = "d_permit";
    private static final String DAEMON_PERMITTING_SP_KEY = "permitted";

    private void initDaemon(Context base) {
        if (config == null) {
            return;
        }

        String processName = getProcessName();
        Logger.v(Logger.TAG, ">>>------------------------->>> processName: " + processName);
        if (TextUtils.isEmpty(processName)) {
            Logger.w(Logger.TAG, "process name is empty");
        } else if (processName.startsWith(config.PERSISTENT_CONFIG.processName)) {
            if (config.limitReboot) {
                checkServiceProcessContinuousBootOverTimes(base, config);
            } else {
                setDaemonPermitting(base, true);
            }
            if (isDaemonPermitting(base)) {
                IKeepAliveProcess.Fetcher.fetchStrategy().onPersistentCreate(base, config);
            }
        } else if (processName.startsWith(config.DAEMON_ASSISTANT_CONFIG.processName)) {
            // checkDaemonProcessContinuousBootOverTimes(base, config);
            if (isDaemonPermitting(base)) {
                IKeepAliveProcess.Fetcher.fetchStrategy().onDaemonAssistantCreate(base, config);
            }
        } else if (processName.startsWith(base.getPackageName())) {
            // checkMainProcessContinuousBootOverTimes(base, config);
            if (isDaemonPermitting(base)) {
                IKeepAliveProcess.Fetcher.fetchStrategy().onInit(base, config);
            }
            if (KeepAliveConfigs.bootReceivedListener != null) {
                KeepAliveConfigs.bootReceivedListener.onReceive(base, new Intent(Intent.ACTION_RUN));
            }
//            KeepAlive.launchAlarm(base);
        }
    }

//    public static void launchAlarm(Context context) {
//        // alarm唤醒
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        if (manager == null) {
//            return;
//        }
//        long INTERVAL_WAKEUP_MS = 60 * 1000; // 60 seconds
//        long triggerAtTime = SystemClock.elapsedRealtime() + INTERVAL_WAKEUP_MS;
//        Intent i = new Intent(context, AutoBootReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            manager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        } else {
//            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        }
//    }

    static String getProcessName() {
        BufferedReader br = null;
        try {
            File file = new File("/proc/self/cmdline");
            br = new BufferedReader(new FileReader(file));
            return br.readLine().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isDaemonPermitting(Context context) {
        SharedPreferences sp = context.getSharedPreferences(DAEMON_PERMITTING_SP_FILENAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(DAEMON_PERMITTING_SP_KEY, true);
    }

    protected boolean setDaemonPermitting(Context context, boolean isPermitting) {
        SharedPreferences sp = context.getSharedPreferences(DAEMON_PERMITTING_SP_FILENAME,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(DAEMON_PERMITTING_SP_KEY, isPermitting);
        return editor.commit();
    }

    private static boolean checkMainProcessContinuousBootOverTimes(Context context,
                                                                   KeepAliveConfigs config) {
        return checkProcessContinuousBootOverTimes(context, config,
                "main_process_boot_times", "main_process_boot_time");
    }

    private static boolean checkServiceProcessContinuousBootOverTimes(Context context,
                                                                      KeepAliveConfigs config) {
        return checkProcessContinuousBootOverTimes(context, config,
                "service_process_boot_times", "service_process_boot_time");
    }

    private static boolean checkDaemonProcessContinuousBootOverTimes(Context context,
                                                                     KeepAliveConfigs config) {
        return checkProcessContinuousBootOverTimes(context, config,
                "daemon_process_boot_times", "daemon_process_boot_time");
    }

    private static boolean checkProcessContinuousBootOverTimes(Context context,
                                                               KeepAliveConfigs config,
                                                               String timesKey,
                                                               String rebootTimeKey) {
        SharedPreferences sp = context.getSharedPreferences(DAEMON_PERMITTING_SP_FILENAME,
                Context.MODE_PRIVATE);
        int times = sp.getInt(timesKey, 0);
        long lastBootTime = sp.getLong(rebootTimeKey, 0);
        long now = System.currentTimeMillis();
        Logger.e(Logger.TAG, "checkCC " + times + " lastTime=" + lastBootTime
                + " diff=" + (now - lastBootTime) + " max=" + config.rebootIntervalMs
                + " times=" + config.rebootMaxTimes);
        if (lastBootTime > 0) {
            if (now - lastBootTime < config.rebootIntervalMs) {
                if (times >= config.rebootMaxTimes) {
                    markProcessBoot(context, 1, timesKey, rebootTimeKey);
                    if (client != null) {
                        client.setDaemonPermitting(context, false);
                    }
                    Logger.e(Logger.TAG, "daemon is not permitted!");
                    return true;
                } else {
                    times++;
                    markProcessBoot(context, times, timesKey, rebootTimeKey);
                }
            } else {
                markProcessBoot(context, 1, timesKey, rebootTimeKey);
            }
        } else {
            markProcessBoot(context, 1, timesKey, rebootTimeKey);
        }
        if (client != null) {
            client.setDaemonPermitting(context, true);
        }
        return false;
    }

    private static void markProcessBoot(Context context, int times, String timesKey,
                                        String rebootTimeKey) {
        SharedPreferences sp = context.getSharedPreferences(DAEMON_PERMITTING_SP_FILENAME,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(timesKey, times);
        editor.putLong(rebootTimeKey, System.currentTimeMillis());
        editor.apply();
    }
}
