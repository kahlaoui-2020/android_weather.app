package fr.m1miage.weather;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import fr.m1miage.weather.model.Weather;

import java.util.Calendar;

import fr.m1miage.weather.common.Common;
import fr.m1miage.weather.model.WeatherForecastResult;
import fr.m1miage.weather.retrofit.IOpenWeatherMap;
import fr.m1miage.weather.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static fr.m1miage.weather.MainActivity.lat;
import static fr.m1miage.weather.MainActivity.lon;


public class forecastActivity extends AppCompatActivity {


    public static Context mContext;

    static RecyclerView recyclerView;
    RecyclerView recyclerView_day;

    ImageButton btnRetour;
    TextView city;

    static RecyclerViewAdapter adapter;
    RecyclerViewAdapterDay adapter_1;

    static ArrayList<ArrayList<Weather>> weatherByDay;

    AlertDialog alert;
    AlertDialog.Builder builder;

    ArrayList<String> list = new ArrayList<>();

    private static final String BroadcastReceiver = "";
    private IntentFilter intentFilter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);


        btnRetour =  findViewById(R.id.btnRetour1);
        city = findViewById(R.id.cityView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView_day = findViewById(R.id.days_list);
        mContext = this;


        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastReceiver);
        Intent networkService = new Intent(this, InternetService.class);
        startService(networkService);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        alert = builder.create();

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setMeasurementCacheEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_day.setLayoutManager(layoutManager);

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchForecastWeatherData();
    }
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


    public BroadcastReceiver recevier = new BroadcastReceiver() {

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

    private void fetchForecastWeatherData() {
        Retrofit retrofit = RetrofitClient.getInstance();
        IOpenWeatherMap myapi = retrofit.create(IOpenWeatherMap.class);
        Call<WeatherForecastResult> main = myapi.getForecasttWeather(lat, lon,"metric", Common.API_ID, "fr");
        main.enqueue(new Callback<WeatherForecastResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<WeatherForecastResult> call, Response<WeatherForecastResult> response) {
                if(response.code() == 404) {
                    Toast.makeText(forecastActivity.this, "Please enter a valid coordinate", Toast.LENGTH_LONG).show();
                }
                else if(!(response.isSuccessful())) {
                    Toast.makeText(forecastActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
                WeatherForecastResult myWeather = response.body();

                city.setText(myWeather.getCity().getName()+","+myWeather.getCity().getCountry());
                ArrayList<Weather> weathers = new ArrayList<>();
                list = new ArrayList<>();
                weatherByDay = new ArrayList<>();



                long dt = myWeather.getList().get(0).getDt();
                Calendar calendar = Calendar.getInstance();
                list.add(Common.convertUnixToDay(dt));


                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int index = 0;
                int day_i ;
                while(index < myWeather.getList().size()) {
                    long dt_i = myWeather.getList().get(index).getDt();
                    calendar.setTimeInMillis(dt_i*1000);
                    day_i = calendar.get(Calendar.DAY_OF_MONTH);


                    String date = Common.convertUnixToTime(dt_i);

                    String temp = String.valueOf(myWeather.getList().get(index).getMain().getTemp());
                    String icon = myWeather.getList().get(index).getWeather().get(0).getIcon();
                    String desc = myWeather.getList().get(index).getWeather().get(0).getDescription();

                    if(day == day_i) {
                        weathers.add(new Weather(date, temp, desc, icon));
                    } else {
                        day = day_i;
                        weatherByDay.add(weathers);
                        weathers = new ArrayList<>();
                        weathers.add(new Weather(date, temp, desc, icon));
                        list.add(Common.convertUnixToDay(dt_i));
                    }
                    index++;
                }
                weatherByDay.add(weathers);

                calendar.setTimeInMillis(dt*1000);
                adapter = new RecyclerViewAdapter(forecastActivity.this, weatherByDay.get(RecyclerViewAdapterDay.position));
                recyclerView.setAdapter(adapter);






                adapter_1 = new RecyclerViewAdapterDay(forecastActivity.this, list);
                recyclerView_day.setAdapter(adapter_1);


            }

            @Override
            public void onFailure(Call<WeatherForecastResult> call, Throwable t) {

            }
        });
    }


}