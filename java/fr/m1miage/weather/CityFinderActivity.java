package fr.m1miage.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.view.inputmethod.EditorInfo.IME_ACTION_GO;
import static fr.m1miage.weather.MainActivity.lat;
import static fr.m1miage.weather.MainActivity.lon;


public class CityFinderActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private String newCity = "";

    private GoogleMap map;
    ImageButton btnRetour2;

    Marker marker;

    AlertDialog alert;
    AlertDialog.Builder builder;

    public static final String BroadcastReceiver = "";
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_finder);
        final EditText searchCity = findViewById(R.id.searchCity);
        ImageView searchCityButton = findViewById(R.id.searchCityButton);

        btnRetour2 =  findViewById(R.id.btnRetour2);


        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastReceiver);
        Intent networkService = new Intent(this, InternetService.class);
        startService(networkService);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        alert = builder.create();

        searchCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCity = searchCity.getText().toString();
                if(newCity.equals(null) || newCity.equals("")) {
                    Toast.makeText(CityFinderActivity.this, "Aucune ville n'a été détecter, merci de saisie une ville valide", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(CityFinderActivity.this, MainActivity.class);
                    intent.putExtra("City", newCity);
                    startActivity(intent);
                    finish();

                }


            }
        });
        searchCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                newCity = searchCity.getText().toString();
                if(actionId == IME_ACTION_GO) {
                    newCity = searchCity.getText().toString();
                    if(newCity.equals(null) || newCity.equals("")) {
                        Toast.makeText(CityFinderActivity.this, "Aucune ville n'a été détecter, merci de saisie une ville valide", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(CityFinderActivity.this, MainActivity.class);
                        intent.putExtra("City", newCity);
                        startActivity(intent);
                        finish();

                    }
                }
                return false;
            }
        });

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        btnRetour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        LatLng pos = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        marker = map.addMarker(new MarkerOptions()
                .position(pos)
        );

        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                marker = map.addMarker(new MarkerOptions().position(latLng));
            }
        });
        map.moveCamera(CameraUpdateFactory.newLatLng(pos));



    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        lat = String.valueOf(marker.getPosition().latitude);
        lon = String.valueOf(marker.getPosition().longitude);
        Intent intent = new Intent(CityFinderActivity.this, MainActivity.class);
        intent.putExtra("City", "2");
        startActivity(intent);
        finish();
        return false;
    }


    private void Internet_OFF() {
        if(alert == null) {
            alert = builder.create();
        }
        alert.setMessage("Vous n'êtes pas connecté(e) à l'internet");
        alert.show();
    }
    private void Internet_ON() {
        if(alert != null && alert.isShowing()) {
            alert.dismiss();
        }
    }


    public android.content.BroadcastReceiver recevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BroadcastReceiver)) {
                if(intent.getStringExtra("isOnline").equals("true")) {
                    Internet_ON();
                }else {
                    Internet_OFF();
                }
            }
        }
    };
}