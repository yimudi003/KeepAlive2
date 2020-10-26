package com.sogou.daemon.component;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;
import com.sogou.daemon.utils.Utils;
import com.sogou.log.Log;

public class DaemonInstrumentation extends Instrumentation {
    public void callApplicationOnCreate(Application application) {
        super.callApplicationOnCreate(application);
        Log.v(Log.TAG, "callApplicationOnCreate");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.v(Log.TAG, "onCreate");
        Utils.fireService(getTargetContext(), DaemonService.class);
    }
}
