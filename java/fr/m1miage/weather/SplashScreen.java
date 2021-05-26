package fr.m1miage.weather;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;




public class SplashScreen extends AppCompatActivity {

    boolean gps_enabled = true;
    boolean network_enabled = false;

    AlertDialog IAlert;
    AlertDialog.Builder builder;

    private IntentFilter intentFilter;

    public static final String BroadcastReceiver = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    100);
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastReceiver);
        Intent networkService = new Intent(this, InternetService.class);
        startService(networkService);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);



    }


    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(recevier, intentFilter);

    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(recevier);

    }

    private void Internet_OFF() {
        if(IAlert == null) {
            IAlert = builder.create();
        }
        IAlert.setMessage("Vous n'êtes pas connecté(e) à l'internet");
        IAlert.show();

    }
    private void Internet_ON() {
        if(IAlert != null && IAlert.isShowing()) IAlert.dismiss();
        if(gps_enabled) {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();

        }
    }



    public android.content.BroadcastReceiver recevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BroadcastReceiver)) {
                 if(intent.getStringExtra("isOnline").equals("true")) {
                     network_enabled = true;
                     Internet_ON();
            }else {
                network_enabled = false;
                Internet_OFF();

            }
            }
        }
    };




}
