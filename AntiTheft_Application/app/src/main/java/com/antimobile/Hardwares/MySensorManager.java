package com.antimobile.Hardwares;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.antimobile.MainHelpers.GMailSender;
import com.antimobile.activity.StopActivity;
import com.antimobile.retrofit.UserDetails;
import com.antimobile.services.PowerButtonService;

import java.util.List;

/**
 * Created by saksham on 8/24/2017.
 */

public class MySensorManager {

    Context context;
    SensorManager sm; //sensor name, and sensor onclick listener
    Sensor proximty;
    SensorEventListener psel;
    public static final String TAG = "MySensorManager";
    onWakeUp onWakeUp;
    PowerManager.WakeLock wl;
    BackgroundAudio backgroundAudio;
    PowerButtonService powerButtonService;
    UserDetails userDetails;
    StopActivity stopActivity,powerbutton;

    public interface onWakeUp {

        void setOnWakeUp();
    }


    public MySensorManager(Context context) {
        this.context = context;
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        backgroundAudio = new BackgroundAudio(context);
        powerButtonService = new PowerButtonService();
        userDetails = new UserDetails(context);
        stopActivity = new StopActivity();
        powerbutton = new StopActivity();
    }

    public void startProximity(final PowerManager.WakeLock wl) {

        proximty = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (wl.isHeld()) {
            Log.d(TAG, "startProximity: held");
        } else {
            Log.d(TAG, "startProximity: not held");
        }


        psel = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {


                if (event.values[0] == 0) {
                    Log.d(TAG, "onSensorChanged: 0" + ", device INACTIVE");
                 //   wl.acquire();

                } else {
                    //TODO wake the device up
                   // wl.release();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            backgroundAudio.startAudio();
                            sendMessage();
                           sendMySMS();
                            powerbutton.powerbutton();

                        }
                    }, 10000);

                    Log.d(TAG, "onSensorChanged: 6" + ", device ACTIVE");
                  //  onWakeUp.setOnWakeUp();
                    if (wl.isHeld()) {
                        Log.d(TAG, "startProximity: held");

                    } else {
                        Log.d(TAG, "startProximity: not held");

                    }

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

                //do nothing here
            }
        };
        Log.d(TAG, "registering the proximity sensor");
        sm.registerListener(psel, proximty, 1000);

    }

    public void stopProximity() {
backgroundAudio.stopAudio();
        Log.d(TAG, "unregistering the proximity sensor: ");
        sm.unregisterListener(psel, proximty);
    }
    private void sendMessage() {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        dialog.show();
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("heyb204@gmail.com", "Hack3R0007");
                    sender.sendMail("AntiMobileTheft ",
                            "Geo location Latitude"+userDetails.getCategory()+" Longitude:"+userDetails.getBrand(),
                          "AntiMobileTheft" ,
                            userDetails.getEmailKey());
                    sender.addAttachment("/root/sdcard/Pictures/image.jpg");
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }

    public void sendMySMS() {

        String phone = userDetails.getMobile();
        String message = "Geo location Latitude"+userDetails.getCity()+" Longitude:"+ userDetails.getBrand();


        //Check if the phoneNumber is empty
        if (phone.isEmpty()) {
            Toast.makeText(context, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
        } else {

            SmsManager sms = SmsManager.getDefault();
            // if message length is too long messages are divided
            List<String> messages = sms.divideMessage(message);

            for (String msg : messages) {

                PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0);
                sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);

            }
        }
    }
}
