package com.keepalive.daemon.core.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String getProcessName() {
        BufferedReader mBufferedReader = null;
        try {
            File file = new File("/proc/self/cmdline");
            mBufferedReader = new BufferedReader(new FileReader(file));
            return mBufferedReader.readLine().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (mBufferedReader != null) {
                try {
                    mBufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void fireService(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        try {
            context.startService(intent);
        } catch (Exception e) {
            context.bindService(intent, new ServiceConnection() {
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                }

                public void onServiceDisconnected(ComponentName componentName) {
                }
            }, 0);
        }
    }

    public static void getCurrSOLoaded() {
        List<String> allSOLists = null;
        // 当前应用的进程ID
        int pid = android.os.Process.myPid();
        String path = "/proc/" + pid + "/maps";
        Logger.v(Logger.TAG, "maps path: " + path);
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            allSOLists = readFileByLines(file.getAbsolutePath());
        } else {
            Logger.w(Logger.TAG, "不存在[" + path + "]文件.");
        }

        if (allSOLists == null || allSOLists.size() == 0) {
            return;
        }
    }

    private static List<String> readFileByLines(String fileName) {
        List<String> allSOLists = new ArrayList<>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                if (tempString.endsWith(".so")) {
                    int index = tempString.indexOf("/");
                    if (index != -1) {
                        String str = tempString.substring(index);
                        if (!allSOLists.contains(str)) {
                            Logger.v(Logger.TAG, "str: " + str);
                            // 所有so库（包括系统的，即包含/system/目录下的）
                            allSOLists.add(str);
                        }
                    }
                }
            }
//            Logger.v(Logger.TAG, "allSOLists: " + allSOLists);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return allSOLists;
    }
}
