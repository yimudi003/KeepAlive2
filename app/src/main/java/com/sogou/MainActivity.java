package com.sogou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import com.sogou.daemon.R;
import com.sogou.interestclean.notification.NotifyResidentService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContextCompat.startForegroundService(this, new Intent(this, NotifyResidentService.class));
    }
}
