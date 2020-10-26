package com.sogou;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.sogou.daemon.JavaDaemon;
import com.sogou.daemon.component.DaemonInstrumentation;
import com.sogou.daemon.component.DaemonReceiver;
import com.sogou.daemon.component.DaemonService;
import com.sogou.log.Log;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.v(Log.TAG, "attachBaseContext");
        JavaDaemon.a().a(this,
                new Intent(this, DaemonService.class),
                new Intent(this, DaemonReceiver.class),
                new Intent(this, DaemonInstrumentation.class)
        );
        getPackageName();
        String[] strArr = {"daemon", "assist1", "assist2"};
        JavaDaemon.a().a(this, strArr);
    }
}
