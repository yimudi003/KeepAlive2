package com.sogou.daemon.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.sogou.daemon.utils.Utils;
import com.sogou.log.Log;

public class AssistService2 extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.v(Log.TAG, "onCreate: ");
    }
}
