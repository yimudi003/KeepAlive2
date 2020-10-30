package com.keepalive.daemon.core.component;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.keepalive.daemon.core.utils.Logger;

public class DaemonInstrumentation extends Instrumentation {
    @Override
    public void callApplicationOnCreate(Application application) {
        super.callApplicationOnCreate(application);
        Logger.v(Logger.TAG, "callApplicationOnCreate");
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Logger.v(Logger.TAG, "onCreate");
        ContextCompat.startForegroundService(getTargetContext(),
                new Intent(getTargetContext(), DaemonService.class));
    }
}
