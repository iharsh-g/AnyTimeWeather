package com.android.example.anytimeweather.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.anytimeweather.Models.ForecastWeatherHourly;
import com.android.example.anytimeweather.R;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ForecastWeatherHourlyAdapter
        extends RecyclerView.Adapter<ForecastWeatherHourlyAdapter.ForecastWeatherHourlyViewHolder>{

    private ArrayList<ForecastWeatherHourly> itemsList;
    private Context mContext;

    public ForecastWeatherHourlyAdapter(Context context, ArrayList<ForecastWeatherHourly> weatherHourlyArrayList){
        this.mContext = context;
        this.itemsList = weatherHourlyArrayList;
    }

    @NonNull
    @Override
    public ForecastWeatherHourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_forecast_hours, parent, false);
        return new ForecastWeatherHourlyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ForecastWeatherHourlyViewHolder holder, int position) {
        ForecastWeatherHourly current = itemsList.get(position);

        Glide.with(mContext).load(current.getHourlyIcon()).into(holder.mIcon);
        holder.setTime(current.getHourlyTime());

        SharedPreferences sharedPreferences1 = mContext.getSharedPreferences("prefTempUnits", MODE_PRIVATE);
        if(sharedPreferences1.getString("unit", "Celsius").equals("Celsius")){
            holder.mTemp.setText(Double.toString(current.getHourlyCel()));
        } else {
            holder.mTemp.setText(Double.toString(current.getHourlyFeh()));
        }

        holder.mRain.setText("" + current.getHourlyRain());
        holder.mSnow.setText("" + current.getHourlySnow());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ForecastWeatherHourlyViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIcon;
        private TextView mTime, mTemp, mRain, mSnow;

        public ForecastWeatherHourlyViewHolder(@NonNull View itemView) {
            super(itemView);

            mIcon = itemView.findViewById(R.id.iv_hours);
            mTime = itemView.findViewById(R.id.hourly_time);
            mTemp = itemView.findViewById(R.id.temp);
            mRain = itemView.findViewById(R.id.rain_hourly);
            mSnow = itemView.findViewById(R.id.snow_hourly);
        }

        private void setTime(String time){
            String actualTime = "";
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

            Date date2 = null;
            try {
                date2 = readDate.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat writeDate = new SimpleDateFormat("yyyy-MM-dd 'T' hh:mm aa", Locale.US);
            time = writeDate.format(date2);

            if (time.contains(" T ")) {
                String[] parts = time.split(" T ");
                actualTime = parts[1];
            }

            mTime.setText(actualTime);
        }
    }
}
