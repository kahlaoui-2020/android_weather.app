package fr.m1miage.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import fr.m1miage.weather.common.Common;
import fr.m1miage.weather.databinding.ActivityMainBinding;
import fr.m1miage.weather.model.WeatherResult;
import fr.m1miage.weather.retrofit.IOpenWeatherMap;
import fr.m1miage.weather.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static fr.m1miage.weather.retrofit.RetrofitClient.URL_ICON;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView vitesseVentValeur, humidityValeur, tempRessentiValeur;
    ImageView chercher, gps;
    TextView favoris;
    Button previsions5jours;
    TextView jour, date, heure, temp, weatherType;
    ImageView weatherIcon;
    TextView city;
    ImageView save;

    String newCity = null;
    boolean gps_enabled;

    public static String lat, lon;

    LocationManager locationManager;

    ProgressDialog progress;

    SharedPreferences pref;
    Set<String> listFavoris;

    AlertDialog alert;
    AlertDialog IAlert, GPSAlert;
    AlertDialog.Builder builder;

    public static final String BroadcastReceiver = "";
    private IntentFilter intentFilter, GPS_Intent;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("favoris", MODE_PRIVATE);


        vitesseVentValeur = findViewById(R.id.vitesseVentValeur);
        humidityValeur = findViewById(R.id.humidityValeur);
        tempRessentiValeur = findViewById(R.id.tempRessentiValeur);
        chercher = findViewById(R.id.chercher);
        gps = findViewById(R.id.gps);
        favoris = findViewById(R.id.favoris);
        previsions5jours = findViewById(R.id.previsions5jours);
        date = findViewById(R.id.date);
        jour = findViewById(R.id.aujoudhui);
        heure = findViewById(R.id.heure);
        temp = findViewById(R.id.temp);
        weatherType = findViewById(R.id.weatherType);
        weatherIcon = findViewById(R.id.weatherIcon);
        city = findViewById(R.id.city);
        save = findViewById(R.id.save);


        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastReceiver);
        Intent networkService = new Intent(this, InternetService.class);
        startService(networkService);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);



        progress = new ProgressDialog(MainActivity.this);
        progress.setMax(100);
        progress.setMessage("Data is loading...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();





        previsions5jours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(MainActivity.this, forecastActivity.class);
                startActivity(activity);
            }
        });

        chercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityFinderActivity.class);
                startActivity(intent);
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                localise();
                gps.setImageResource(R.drawable.outline_location_on_black_24);
                newCity = null;
            }
        });



        favoris.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent activity = new Intent(MainActivity.this, ListFavoris.class);
                startActivity(activity);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                listFavoris = new HashSet<>();
                listFavoris = pref.getStringSet("list", new HashSet<String>());
                listFavoris.add(city.getText().toString());
                SharedPreferences.Editor editor = pref.edit();
                editor.putStringSet("list", listFavoris);
                editor.apply();
                save.setImageResource(R.drawable.outline_bookmark_added_black_24);

            }
        });

    }

    private void Internet_OFF() {
        if(IAlert == null) {
            IAlert = builder.create();
        }
        IAlert.setMessage("Vous n'êtes pas connecté(e) à l'internets");
        IAlert.show();
    }
    private void Internet_ON() {
        if(IAlert != null && IAlert.isShowing()) {
            IAlert.dismiss();
        }
    }


    public BroadcastReceiver recevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BroadcastReceiver)) {
                if (intent.getStringExtra("isOnline").equals("true")) {
                    Internet_ON();
                } else {
                    Internet_OFF();
                }
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(recevier, intentFilter);

        Intent intent = getIntent();
        this.newCity = intent.getStringExtra("City");

        // For GPS et Bookmark ImageView
        if (newCity == null) {
            localise();
            gps.setImageResource(R.drawable.outline_location_on_black_24);
        } else if(newCity.equals("2")) {
            fetchCurrentWeatherData();
        } else {
           getWeatherForNewCity(this.newCity);
            gps.setImageResource(R.drawable.outline_location_off_black_24);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(recevier);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(recevier, intentFilter);
    }

    protected void verifyCityBookemarked(String city) {
        listFavoris = new HashSet<>();
        listFavoris = pref.getStringSet("list", new HashSet<String>());
        if (listFavoris.contains(city)) {
            save.setImageResource(R.drawable.outline_bookmark_added_black_24);
        } else {
            save.setImageResource(R.drawable.outline_bookmark_add_black_24);

        }
    }

    protected void localise() {

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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);



    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        if(alert != null) {
            alert.dismiss();
        }
    }


    @Override
    public void onProviderDisabled(@NonNull String provider) {
        if(alert == null) {
            alert = builder.create();
        }
        alert.setMessage("Le GPS n'est pas activé");
        alert.show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder gps = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addr = gps.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            lat = String.valueOf(addr.get(0).getLatitude());
            lon = String.valueOf(addr.get(0).getLongitude());
            fetchCurrentWeatherData();
        }catch(Exception e) {
            Log.d("GPS Status : ","GPS locked down");
        }
    }

    public void getWeatherForNewCity(String newCity) {
        Retrofit retrofit = RetrofitClient.getInstance();
        IOpenWeatherMap myapi = retrofit.create(IOpenWeatherMap.class);
        Call<WeatherResult> main = myapi.getCityWeather(newCity,"metric", Common.API_ID, "fr");

        main.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                if(response.code() == 404) {
                    Toast.makeText(MainActivity.this, "Please enter a valid coordinate", Toast.LENGTH_LONG).show();
                }
                else if(!(response.isSuccessful())) {
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
                WeatherResult myWeather = response.body();
                city.setText(myWeather.getName());
                vitesseVentValeur.setText(String.valueOf(myWeather.getWind().getSpeed())+" km/h");
                humidityValeur.setText(String.valueOf(myWeather.getMain().getHumidity())+" g/m3");

                temp.setText(String.valueOf(myWeather.getMain().getTemp())+"°C");
                tempRessentiValeur.setText(String.valueOf(myWeather.getMain().getFeels_like())+"°C");
                weatherType.setText(myWeather.getWeather().get(0).getMain());

                Picasso.get().load(URL_ICON + myWeather.getWeather().get(0).getIcon() +"@2x.png").resize(300,300).into(weatherIcon);

                Date dateCurr = new Date();
                SimpleDateFormat jourStr = new SimpleDateFormat("EEEE");
                SimpleDateFormat dateStr = new SimpleDateFormat("dd MMMM");
                SimpleDateFormat heureStr = new SimpleDateFormat("kk:mm");
                jour.setText(jourStr.format(dateCurr));
                date.setText(dateStr.format(dateCurr));
                heure.setText(heureStr.format(dateCurr));

                progress.dismiss();
                verifyCityBookemarked(city.getText().toString());


            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage().toString(), Toast.LENGTH_LONG).show();
                progress.dismiss();

            }
        });
    }

    private void fetchCurrentWeatherData() {
       Retrofit retrofit = RetrofitClient.getInstance();
       IOpenWeatherMap myapi = retrofit.create(IOpenWeatherMap.class);
        Call<WeatherResult> main = myapi.getCurrentWeather(lat, lon,"metric", Common.API_ID, "fr");



        main.enqueue(new Callback<WeatherResult>() {@Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                if(response.code() == 404) {
                    Toast.makeText(MainActivity.this, "Please enter a valid coordinate", Toast.LENGTH_LONG).show();
                }
                else if(!(response.isSuccessful())) {
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
                WeatherResult myWeather = response.body();

                city.setText(myWeather.getName());
                vitesseVentValeur.setText(String.valueOf(myWeather.getWind().getSpeed())+" km/h");
                humidityValeur.setText(String.valueOf(myWeather.getMain().getHumidity())+" g/m3");

                temp.setText(String.valueOf(myWeather.getMain().getTemp())+"°C");
                tempRessentiValeur.setText(String.valueOf(myWeather.getMain().getFeels_like())+"°C");
                weatherType.setText(myWeather.getWeather().get(0).getMain());

                Picasso.get().load(URL_ICON + myWeather.getWeather().get(0).getIcon() +"@2x.png").resize(300,300).into(weatherIcon);

                Date dateCurr = new Date();
                SimpleDateFormat jourStr = new SimpleDateFormat("EEEE");
                SimpleDateFormat dateStr = new SimpleDateFormat("dd MMMM");
                SimpleDateFormat heureStr = new SimpleDateFormat("kk:mm");
                jour.setText(jourStr.format(dateCurr));
                date.setText(dateStr.format(dateCurr));
                heure.setText(heureStr.format(dateCurr));
                verifyCityBookemarked(city.getText().toString());



            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}

