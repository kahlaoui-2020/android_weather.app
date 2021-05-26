package fr.m1miage.weather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerViewAdapterDay extends RecyclerView.Adapter<RecyclerViewAdapterDay.ViewHolder>{


    public static int position;

    private Context mContext;
    private ArrayList<String> list;
    public RecyclerViewAdapterDay (Context context, ArrayList<String> list) {
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterDay.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.day_layout, parent, false);
        return new RecyclerViewAdapterDay.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String day = list.get(position);

        holder.day.setText(day);



    }



    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        final Button day;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                    Log.d("Adapter position : ",""+position);
                    forecastActivity.adapter = new RecyclerViewAdapter(forecastActivity.mContext, forecastActivity.weatherByDay.get(RecyclerViewAdapterDay.position));
                    forecastActivity.recyclerView.setAdapter(forecastActivity.adapter);

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                }
            });
        }


    }
}
