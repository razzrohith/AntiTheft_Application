package com.antimobile.Hardwares;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.antimobile.R;
import com.antimobile.services.Constants;


/**
 * Created by saksham on 7/30/2017.
 */

public class BackgroundAudio {

    Context context;
    AudioManager am;
    static MediaPlayer mp;
    public static final String TAG = "BackgroundAudio";
    public static boolean isInitialise = false;


    public BackgroundAudio(Context context) {

        this.context = context;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mp = MediaPlayer.create(context, R.raw.music);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startAudio() {

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(10000, VibrationEffect.DEFAULT_AMPLITUDE));

        int currVolume = Constants.SharedPrefsConstants.getValue("audioVolume", context);

        am.setStreamVolume(AudioManager.STREAM_MUSIC,
                //am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) + 5, //will not set when earphone is plugged IN
                getVolume(currVolume),
                1);

        Log.d(TAG, "startAudio: "+currVolume+context);
        mp.start();
        isInitialise = true;

    }


    public void stopAudio() {

        isInitialise = false;

        mp.stop();
        mp.release();
        mp = MediaPlayer.create(context, R.raw.music);
    }

    public static boolean isInitialised() {
        return isInitialise;
    }

    public void destroyInstance() {

        mp.release();

    }

    //convert volume in percentage to integer (1-15)
    private int getVolume(int currVolume) {

        return ((currVolume * 15)/100) ;
    }




}