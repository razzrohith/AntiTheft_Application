package com.antimobile.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.antimobile.Hardwares.MySensorManager;
import com.antimobile.R;
import com.antimobile.retrofit.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    Vibrator v1 ;
    int check=2;
    private UserDetails userDetails;
    private TextView tv_email,tv_phone;
    CountDownTimer cdt;
    Button btn_start;
    MySensorManager proximitySensor;

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        tv_phone = findViewById(R.id.tv_phone);
        tv_email = findViewById(R.id.tv_email);
        btn_start = findViewById(R.id.btn_start);
        userDetails = new UserDetails(this);
        tv_email.setText(userDetails.getEmailKey());
        tv_phone.setText(userDetails.getMobile());
        proximitySensor = new MySensorManager(StartActivity.this);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, StopActivity.class);
                startActivity(intent);
                finish();


            }
        });

        AudioManager am =
                (AudioManager) StartActivity.this.getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            default:
                return super.onOptionsItemSelected(item);
            case R.id.menu_main_setting:

                Intent intent =new Intent(StartActivity.this,SettingActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.changedetails:

                Intent intent1 =new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                return true;
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            // this is method which detect press even of button
            event.startTracking(); // Needed to track long presses
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            // Here we can detect long press of power button
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
    @Override
    public void onBackPressed() {
     //   Toast.makeText(this, "You Cannot close the application", Toast.LENGTH_SHORT).show();
    }
}
