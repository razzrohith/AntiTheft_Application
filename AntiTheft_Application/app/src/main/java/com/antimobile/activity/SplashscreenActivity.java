package com.antimobile.activity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.antimobile.R;
import com.antimobile.retrofit.UserDetails;


public class SplashscreenActivity extends AppCompatActivity {//implements LocationListener
    boolean lastTime = false;
   // GoogleMap googleMap;

    private LocationManager locMan;
    private UserDetails userDetails;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        userDetails = new UserDetails(this);
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        if (checkAndRequestPermissions(this)) {

//        }

        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(2 * 1000);
                    initApp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        background.start();
    }

    private void initApp() {
        if (userDetails.getCity().equalsIgnoreCase("1")) {
            Intent intent = new Intent(SplashscreenActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        } else if (userDetails.isLoggedIn()) {

            Intent intent = new Intent(SplashscreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {
            Intent intent = new Intent(SplashscreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

//    void enableGPS() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//            } else
//                checkIfGPSIsOn(locMan, this);
//        } else if (userDetails.isLoggedIn()) {
//            Intent intent = new Intent(SplashscreenActivity.this, FirstActiivty.class);
//            startActivity(intent);
//            finish();
//        } else {
//            Intent intent = new Intent(SplashscreenActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//
//    }

//    @SuppressWarnings({"MissingPermission"})
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
//
//                HashMap<String, Integer> perms = new HashMap<>();
//                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < permissions.length; i++)
//                        perms.put(permissions[i], grantResults[i]);
//                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
//                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                            && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                        initApp();
//                    } else {
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
//                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
//                            showDialogOK("Permissions are required for this app",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            switch (which) {
//                                                case DialogInterface.BUTTON_POSITIVE:
//                                                    checkAndRequestPermissions(SplashscreenActivity.this);
//                                                    break;
//                                                case DialogInterface.BUTTON_NEGATIVE:
//                                                    // proceed with logic by disabling the related features or quit the app.
//                                                    finish();
//                                                    break;
//                                            }
//                                        }
//                                    });
//                        } else {
//                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
//                        }
//                    }
//                }
//            }
//        }
//    }

//    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(this)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", okListener)
//                .create()
//                .show();
//    }
//
//    private void explain(String msg) {
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setMessage(msg)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        //  permissionsclass.requestPermission(type,code);
//                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.repayer")));
//
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        finish();
//                    }
//                });
//        dialog.show();
//    }
    /* switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    Intent intent = new Intent(SplashscreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, this);

                } else {

                    // permission denied
                    Toast.makeText(this, "GPS Disabled, enable GPS and allow app to use the service. Or app will not work properly.", Toast.LENGTH_LONG).show();
                    enableGPS();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }*/


//    protected void checkIfGPSIsOn(LocationManager manager, Context context) {
//        if (!manager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("Location Manager");
//            builder.setMessage("Repayr requires a device with GPS to work.");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finish();
//                    initApp();
//                    if (ActivityCompat.checkSelfPermission(SplashscreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SplashscreenActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, SplashscreenActivity.this);
//                    }
//                }
//            });
//            builder.create().show();
//        } else {
//            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                //Ask the user to enable GPS
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Location Manager");
//                builder.setMessage("Repayr would like to enable your device's GPS?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Launch settings, allowing user to make a change
//                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(i);
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //No location service, no Activity
//                        finish();
//                    }
//                });
//                builder.create().show();
//            }
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
//
//    void getPermissionLastTime() {
//        if (!lastTime) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
//
//                } else
//                    checkIfGPSIsOn(locMan, this);
//            } else {
//                locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, this);
//            }
//        }
//
//        lastTime = true;
//    }
//
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        assert connectivityManager != null;
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
//
//    @SuppressWarnings({"MissingPermission"})
//    private Location getLastKnownLocation() {
//        locMan = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//        List<String> providers = locMan.getProviders(true);
//        Location bestLocation = null;
//        for (String provider : providers) {
//            Location l = locMan.getLastKnownLocation(provider);
//            if (l == null) {
//                continue;
//            }
//            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
//                // Found best last known location: %s", l);
//                bestLocation = l;
//            }
//        }
//        return bestLocation;
//    }
//
//
//    @SuppressWarnings({"MissingPermission"})
//    @Override
//    protected void onResume() {
//        super.onResume();
////        if (googleMap != null) {
////            locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);
////        }
//    }
//
//    @SuppressWarnings({"MissingPermission"})
//    @Override
//    protected void onPause() {
//        super.onPause();
////        if (googleMap != null) {
////            locMan.removeUpdates(this);
////        }
//    }
//
//    public  boolean checkAndRequestPermissions(final Activity context) {
//        int ExtstorePermission = ContextCompat.checkSelfPermission(context,
//                Manifest.permission.READ_EXTERNAL_STORAGE);
//        int cameraPermission = ContextCompat.checkSelfPermission(context,
//                Manifest.permission.CAMERA);
//        int finePermission = ContextCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        int coarsePermission = ContextCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_COARSE_LOCATION);
//        int callPermission = ContextCompat.checkSelfPermission(context,
//                Manifest.permission.CALL_PHONE);
//        List<String> listPermissionsNeeded = new ArrayList<>();
//        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.CAMERA);
//        }
//        if (ExtstorePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if (finePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//        if (coarsePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        }
//        if (callPermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
//        }
//        if (!listPermissionsNeeded.isEmpty()) {
//            ActivityCompat.requestPermissions(context, listPermissionsNeeded
//                            .toArray(new String[listPermissionsNeeded.size()]),
//                    REQUEST_ID_MULTIPLE_PERMISSIONS);
//            return false;
//        }
//        return true;
//    }

}
