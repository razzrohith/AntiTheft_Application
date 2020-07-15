package com.antimobile.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class ScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            wasScreenOn = false;
            Toast.makeText(context, "screen off", Toast.LENGTH_SHORT).show();

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            wasScreenOn = true;
            Toast.makeText(context, "screen on", Toast.LENGTH_SHORT).show();

        } else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){

            String url = "http://www.stackoverflow.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            i.setData(Uri.parse(url));
            context.startActivity(i);
        }
    }
}
