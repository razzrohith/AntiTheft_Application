package com.antimobile.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antimobile.R;
import com.antimobile.retrofit.UserDetails;
import com.antimobile.services.MyAdminReceiver;


public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SMS = 0;
    private UserDetails userDetails;
    private Button btn_next;
    private EditText et_email,et_phone;

    private DevicePolicyManager mgr=null;
    private ComponentName cn=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userDetails = new UserDetails(this);
        btn_next = findViewById(R.id.btn_next);
        et_email = findViewById(R.id.et_email);

        et_phone = findViewById(R.id.et_phone);

        cn=new ComponentName(this, MyAdminReceiver.class);
        mgr=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);



        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mgr.isAdminActive(cn)) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int hasSMSPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
                        if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                            if (!shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                                showMessageOKCancel("You need to allow access to Send SMS",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                                                            REQUEST_SMS);
                                                }
                                            }
                                        });
                                return;
                            }
                            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                                    REQUEST_SMS);
                            return;
                        }
                        else {
                            String email = et_email.getText().toString();
                            String mobile = et_phone.getText().toString();

                            if(email.isEmpty())
                            {
                                Toast.makeText(LoginActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                            }
                            else if(mobile.isEmpty()) {
                                Toast.makeText(LoginActivity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                            }

                            else if(mobile.length()<10) {
                                Toast.makeText(LoginActivity.this, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                userDetails.setCity("1");
                                userDetails.setEmailKey(email);
                                userDetails.setMobile(mobile);
                                Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }


                    }
                }
                else {
                    Intent intent=
                            new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            getString(R.string.device_admin_explanation));
                    startActivity(intent);
                }

            }
        });
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
