package com.keepalive.daemon.core;

import android.os.Bundle;

interface IMonitorService {
    void processMessage(in Bundle bundle);
}