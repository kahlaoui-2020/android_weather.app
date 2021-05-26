package fr.m1miage.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class RecyclerViewAdapterFavoris  extends RecyclerView.Adapter<RecyclerViewAdapterFavoris.ViewHolder>{


    private Context mContext;
    private ArrayList<String> favoris;
    public RecyclerViewAdapterFavoris(Context context, ArrayList<String> list) {
        this.mContext = context;
        this.favoris = list;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterFavoris.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.favoris_layout, parent, false);
        return new RecyclerViewAdapterFavoris.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterFavoris.ViewHolder holder, int position) {
        String favori = favoris.get(position);
        holder.city.setText(favori);

    }

    @Override
    public int getItemCount() {
        return this.favoris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView city;
        String newCity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.city = itemView.findViewById(R.id.favoris_city);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    newCity = String.valueOf(city.getText());
                    intent.putExtra("City", newCity);
                    // Toast.makeText(mContext, ""+newCity, Toast.LENGTH_LONG).show();
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();

                }
            });

        }
    }
}
