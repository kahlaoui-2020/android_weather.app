package fr.m1miage.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import fr.m1miage.weather.model.Weather;

import static fr.m1miage.weather.retrofit.RetrofitClient.URL_ICON;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Weather> list;
    public RecyclerViewAdapter(Context context, ArrayList<Weather> list) {
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.weather_item, parent, false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weather weather = list.get(position);

        holder.temps.setText(weather.getId());
        holder.temp.setText(weather.getTemp()+"°C");
        Picasso.get().load(URL_ICON + weather.getIcon() +"@2x.png").resize(300,300).into(holder.icon);
        holder.desc.setText(weather.getDescription());



    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView temp;
        final TextView temps;
        final ImageView icon;
        final TextView desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.temp = itemView.findViewById(R.id.temp_h);
            this.temps = itemView.findViewById(R.id.temps);
            this.icon =  itemView.findViewById(R.id.iconWeather);
            this.desc =  itemView.findViewById(R.id.descWeather);
        }
    }

}
