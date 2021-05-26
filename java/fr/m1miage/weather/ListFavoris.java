package fr.m1miage.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashSet;

public class ListFavoris extends AppCompatActivity {

    SharedPreferences pref;
    ArrayList<String> listFavoris;
    ImageButton btnRetour;
    RecyclerView recyclerView;
    RecyclerViewAdapterFavoris adapter;


    AlertDialog alert;
    AlertDialog.Builder builder;

    public static final String BroadcastReceiver = "";
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_favoris);

        btnRetour =  findViewById(R.id.btnRetour2);

        pref = getSharedPreferences("favoris", MODE_PRIVATE);

        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastReceiver);
        Intent networkService = new Intent(this, InternetService.class);
        startService(networkService);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        alert = builder.create();


        listFavoris = new ArrayList<>();
        listFavoris.addAll(pref.getStringSet("list", new HashSet<String>()));

        recyclerView = findViewById(R.id.favorisView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecyclerViewAdapterFavoris(ListFavoris.this, listFavoris);
        recyclerView.setAdapter(adapter);

        btnRetour.setOnClickListener(new View.OnClickListener() {
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
}