package com.sogou;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.keepalive.daemon.core.notification.NotifyResidentService;
import com.keepalive.daemon.core.utils.Logger;
import com.sogou.daemon.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity.this, NotifyResidentService.class);
                    intent.putExtra("noti_title", "Test");
                    intent.putExtra("noti_text", "Hello,world!");
                    intent.putExtra("noti_activity", MainActivity.class.getName());
                    ContextCompat.startForegroundService(MainActivity.this, intent);
                } catch (Throwable th) {
                    Logger.e(Logger.TAG, "failed to start foreground service: " + th.getMessage());
                }
            }
        });
    }
}
