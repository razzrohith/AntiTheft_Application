package com.antimobile.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.antimobile.BuildConfig;
import com.antimobile.Hardwares.BackgroundAudio;
import com.antimobile.Hardwares.MySensorManager;
import com.antimobile.Hardwares.WakeUpReceiver;
import com.antimobile.R;
import com.antimobile.retrofit.UserDetails;
import com.antimobile.services.Constants;
import com.antimobile.services.MyAdminReceiver;
import com.antimobile.services.PhoneUnlockReceiver;
import com.antimobile.services.PowerButtonService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class StopActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PRIVATE_FLAG_PREVENT_SYSTEM_KEYS = 1;
    private static final int PRIVATE_FLAG_PREVENT_POWER_KEY = 1;
    Button btnStop;
    WakeUpReceiver mReceiver;
    public static final String TAG = "StopActivity";
    BackgroundAudio backgroundAudio;
    IntentFilter i;
    boolean toggle = true;
    CountDownTimer waitTime;
    MySensorManager proximitySensor;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    CountDownTimer cdt;
    int currSleepTime = 0;
    PowerButtonService powerButtonService;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private String Latitude,Longitude;
    // location last updated time
    private String mLastUpdateTime;
    PowerManager pm;
    PowerManager.WakeLock wl;
    private UserDetails userDetails;

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    private BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        powerbutton();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        registerReceiver(new PhoneUnlockReceiver(), new IntentFilter("android.intent.action.USER_PRESENT"));
        /*camera = android.hardware.Camera.open();
        params = camera.getParameters();*/
        userDetails = new UserDetails(this);
        powerButtonService = new PowerButtonService();


        //to keep the CPU Awake for longer duration of Time
        pm = (PowerManager) getSystemService(POWER_SERVICE);
        //wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag");

        //not needed in newer device, but is recommended to use as per Android Documentation
        PreferenceManager.setDefaultValues(this, R.xml.pre, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Context context = this;
        Log.d(TAG, "onCreate: " + sharedPreferences.getString("audioVolume", "3") + context);
        Log.d(TAG, "onCreate: " + sharedPreferences.getString("wakeTime", "3"));

        i = new IntentFilter();
        restoreValuesFromBundle(savedInstanceState);
        init();
        startLocationButtonClick();


        btnStop = (Button) findViewById(R.id.btnStop);




        currSleepTime = (Constants.SharedPrefsConstants.getValue("sleepTime", this) * 1000);
        Log.d(TAG, "onCreate: " + currSleepTime);
        cdt = new CountDownTimer(currSleepTime, 1000) {

            long timeSoFar = 0;

            @Override
            public void onTick(long millisUntilFinished) {

                timeSoFar += 1000;
                float result = Float.parseFloat(Integer.toString(((int) (timeSoFar * 100) / currSleepTime)));
                //Log.d(TAG, "onTick: " + result);
                if (result > 100) {
                } else
                {
                    cdt.cancel();
                    waitTime.start();
                }

            }

            @Override
            public void onFinish() {


                //resetting the time so far
                timeSoFar = 0;

            }
        };


        backgroundAudio = new BackgroundAudio(StopActivity.this);


        mDevicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, MyAdminReceiver.class);

        //doing registering of WakeUpReceiver work here
        mReceiver = new WakeUpReceiver(StopActivity.this, new WakeUpReceiver.OnWakeUp() {
            @Override
            public void setOnWakeUp() {

                //this check is important

            }
        });
        Log.d(TAG, "onCreate: " + Constants.HELP);

        //registering the broadcast receiver here
        i.addAction(Intent.ACTION_SCREEN_OFF);
        i.addAction(Intent.ACTION_SCREEN_ON);

        //checks that the keyguard is active or not
        i.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mReceiver, i);
        toggle = false;


        //preventing the CPU from turning off
        wl.acquire();

        //changing states of hardware here
        proximitySensor = new MySensorManager(StopActivity.this) ;//{

        cdt.start();
        backgroundAudio.isInitialise = true;
        int timeToSleep = Constants.SharedPrefsConstants.getValue("sleepTime", StopActivity.this);
        Toast.makeText(StopActivity.this, "Lock your phone in " + timeToSleep + " seconds", Toast.LENGTH_SHORT).show();
//        waitTime.start();

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopLocationButtonClick();

                proximitySensor.stopProximity();
                waitTime.cancel();




                if (backgroundAudio.isInitialise && backgroundAudio.isInitialise) {

                    backgroundAudio.isInitialise = false;
                    waitTime.cancel();

                    if (backgroundAudio != null ) {

                        Log.d(TAG, "onClick: inside if if");
                        backgroundAudio.stopAudio();
                    }
                }
                Intent intent = new Intent(StopActivity.this,StartActivity.class);
                startActivity(intent);
            }
        });



//            @Override
//            public void setOnWakeUp() {
//
//                //TODO screen turn on work to be done here HACK TO WORK ON
//
//                //turning off proximity sensor
//                Log.d(TAG, "setOnWakeUp: turning on the screen");
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//                proximitySensor.stopProximity();
//
//                //turning screen ON
//                PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
//                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
//                screenLock.acquire();
//                screenLock.release();
//
//            }
//        });


        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "description");
        startActivityForResult(intent, 15);

        //used to turn screen OFF and start the proximity after the screen is turned OFF
        waitTime = new CountDownTimer(currSleepTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.d(TAG, "onTick: " + millisUntilFinished);

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFinish() {

                //screen turn off work to be done here
                //changes the system settings timeout for screen to 15 seconds(lowest possible)
                //Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 15000);

                boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                if (isAdmin) {
                   mDevicePolicyManager.lockNow();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(VibrationEffect.createOneShot(10000, VibrationEffect.DEFAULT_AMPLITUDE));
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                           proximitySensor.startProximity(wl);
                        }
                    }, 5000);
                } else {
                    Toast.makeText(getApplicationContext(), "Not Registered as admin", Toast.LENGTH_SHORT).show();
                }


                Log.d(TAG, "onFinish: ");

            }
        };

    }



    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
        sentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Unknown Error";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }

            }
        };
        deliveredStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Message Not Delivered";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Delivered Successfully";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
            }
        };
        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(mReceiver);
        backgroundAudio.destroyInstance();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 15) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
            switch (requestCode) {
                // Check for the integer request code originally supplied to startResolutionForResult().
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            Log.e(TAG, "User agreed to make required location settings changes.");
                            // Nothing to do. startLocationupdates() gets called in onResume again.
                            break;
                        case Activity.RESULT_CANCELED:
                            Log.e(TAG, "User chose not to make required location settings changes.");
                            mRequestingLocationUpdates = false;
                            break;
                    }
                    break;
            }

        }

    }



    @SuppressLint("RestrictedApi")
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

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }


    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            Latitude= String.valueOf(mCurrentLocation.getLatitude());
            Longitude= String.valueOf(mCurrentLocation.getLongitude());
            userDetails.setCategory(String.valueOf(mCurrentLocation.getLatitude()));
            userDetails.setBrand(String.valueOf(mCurrentLocation.getLongitude()));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }



    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(StopActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(StopActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).check();
    }


    public void stopLocationButtonClick() {
        mRequestingLocationUpdates = false;
        stopLocationUpdates();
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();

            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.moveTaskToFront(getTaskId(), 0);
        }
    }

    public void powerbutton()  {

        WindowManager.LayoutParams attr = getWindow().getAttributes();

        try {
            Field privateFlagsField = attr.getClass().getDeclaredField("privateFlags");
            privateFlagsField.setInt(attr, (int) privateFlagsField.get(attr) |
                    PRIVATE_FLAG_PREVENT_SYSTEM_KEYS | PRIVATE_FLAG_PREVENT_POWER_KEY);

            // Just to easily call dispatchWindowAttributesChanged()
            getWindow().setFlags(0, 0);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}