package com.antimobile.services;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhoneUnlockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure()) {

            android.os.Process.killProcess(android.os.Process.myPid());

        }
        else {

        }
    }

}