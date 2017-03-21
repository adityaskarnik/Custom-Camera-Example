package com.aditya.adityapc.customcameraexample;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prathvi on 20/3/17.
 */

public class FirstActivity extends Activity {

    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //permission check loop
        String[] neededPermissions = {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
        };
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.isEmpty()) {
            // All permissions are granted
            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            String[] array = new String[deniedPermissions.size()];
            array = deniedPermissions.toArray(array);
            ActivityCompat.requestPermissions(this, array, REQUEST_PERMISSIONS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
