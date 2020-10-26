package com.sogou.daemon.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.sogou.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
}
