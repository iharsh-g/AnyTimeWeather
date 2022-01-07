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

import com.android.example.anytimeweather.Models.SearchWeather;
import com.android.example.anytimeweather.R;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchWeatherAdapter extends RecyclerView.Adapter<SearchWeatherAdapter.SearchWeatherViewHolder> {

    private Context mContext;
    private ArrayList<SearchWeather> mSearchWeatherArrayList;

    public SearchWeatherAdapter(Context mContext, ArrayList<SearchWeather> arrayList) {
        this.mContext = mContext;
        this.mSearchWeatherArrayList = arrayList;
    }

    @NonNull
    @Override
    public SearchWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_search_weather, parent, false);
        return new SearchWeatherViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SearchWeatherViewHolder holder, int position) {
        SearchWeather current = mSearchWeatherArrayList.get(position); 
        holder.mCountry.setText(current.getSearchWeatherCountry());
        holder.mCapital.setText(current.getSearchWeatherCapital());
        holder.setLocalTimeDate(current.getSearchWeatherLocalTimeAndDate());
        holder.setUpdateTimeDate(current.getSearchWeatherUpdatedTimeAndDate());

        SharedPreferences sharedPreferences1 = mContext.getSharedPreferences("prefTempUnits", MODE_PRIVATE);
        if(sharedPreferences1.getString("unit", "Celsius").equals("Celsius")){
            holder.mTemp1.setText(Double.toString(current.getSearchWeatherCel()));
            holder.mTemp2.setText(Double.toString(current.getSearchWeatherCelFeels()));
        } else {
            holder.mTemp1.setText(Double.toString(current.getSearchWeatherFeh()));
            holder.mTemp2.setText(Double.toString(current.getSearchWeatherFehFeels()));
        }

        holder.mUv.setText(Double.toString(current.getSearchWeatherUv()));
        holder.mHumidity.setText("" + current.getSearchWeatherHumidity());
        holder.mCloud.setText("" + current.getSearchWeatherCloud());
        holder.setIndex(current.getSearchWeatherIndex());

        Glide.with(mContext).load(current.getSearchWeatherIcon()).into(holder.mIcon);
    }

    @Override
    public int getItemCount() {
        return mSearchWeatherArrayList.size();
    }

    class SearchWeatherViewHolder extends RecyclerView.ViewHolder{

        private TextView mCountry, mCapital, mLocalDate, mLocalTime, mUpdatedTime, mUpdatedDate;
        private TextView mTemp1, mTemp2, mUv, mHumidity, mCloud, mIndex;
        private ImageView mIcon;

        public SearchWeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            mCountry = itemView.findViewById(R.id.search_weather_country);
            mCapital = itemView.findViewById(R.id.search_weather_capital);
            mLocalDate = itemView.findViewById(R.id.search_weather_current_date);
            mLocalTime = itemView.findViewById(R.id.search_weather_current_time);
            mUpdatedDate = itemView.findViewById(R.id.search_weather_updated_date);
            mUpdatedTime = itemView.findViewById(R.id.search_weather_updated_time);
            mTemp1 = itemView.findViewById(R.id.search_weather_temp1);
            mTemp2 = itemView.findViewById(R.id.search_weather_temp2);
            mUv = itemView.findViewById(R.id.search_weather_uv_index_val);
            mHumidity = itemView.findViewById(R.id.search_weather_humidity_val);
            mCloud = itemView.findViewById(R.id.search_weather_cloud_val);
            mIndex = itemView.findViewById(R.id.search_weather_aqi_index_val);

            mIcon = itemView.findViewById(R.id.search_weather_img_icon);
        }

        @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
        public void setLocalTimeDate(String dateAndTime) {
            //Getting current day name
            String weekName = "";
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = null;
            try{
                date = inFormat.parse(dateAndTime);
            }catch (ParseException e){
                e.printStackTrace();
            }
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            weekName = outFormat.format(date);


            //Change the month number to month name
            String splitYear = "", splitTime = "", monthName = "";
            int monthNum = 0;
            if (dateAndTime.contains("-")) {
                String[] parts = dateAndTime.split("-");
                splitYear = parts[0];
                monthNum = Integer.parseInt(parts[1]);
                splitTime = parts[2];
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, monthNum);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");  //short form of month names - MMM Eg.Nov
            monthName = simpleDateFormat.format(calendar.getTime());
            dateAndTime = splitYear + "-" + monthName + "-" + splitTime;


            //Change the Format of Date And TIme
            String actualDate = "";
            String actualTime = "";
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MMM-dd HH:mm", Locale.US);

            Date date2 = null;
            try {
                date2 = readDate.parse(dateAndTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat writeDate = new SimpleDateFormat("MMMM dd 'T' hh:mm aa", Locale.US);
            dateAndTime = writeDate.format(date2);

            if (dateAndTime.contains(" T ")) {
                String[] parts = dateAndTime.split(" T ");
                actualDate = parts[0];
                actualTime = parts[1];
            }

            mLocalDate.setText(weekName + ", " + actualDate);
            mLocalTime.setText(actualTime);
        }

        public void setUpdateTimeDate(String dateAndTime) {
            String actualDate = "";
            String actualTime = "";
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

            Date date = null;
            try {
                date = readDate.parse(dateAndTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat writeDate = new SimpleDateFormat("MM/yy, 'T' hh:mm aa", Locale.US);
            dateAndTime = writeDate.format(date);

            if (dateAndTime.contains(" T ")) {
                String[] parts = dateAndTime.split(" T ");
                actualDate = parts[0];
                actualTime = parts[1];
            }

            mUpdatedDate.setText(actualDate);
            mUpdatedTime.setText(actualTime);
        }

        @SuppressLint("SetTextI18n")
        public void setIndex(int index){
            if(index == 1){
                mIndex.setText(index + " (Good)");
            } else if(index == 2){
                mIndex.setText(index + " (Moderate)");
            } else if(index == 3){
                mIndex.setText(index + " (Lightly Unhealthy)");
            } else if(index == 4){
                mIndex.setText(index + " (Unhealthy)");
            } else if(index == 5){
                mIndex.setText(index + " (Very Unhealthy)");
            } else {
                mIndex.setText(index + " (Hazardous)");
            }
        }
    }
}
