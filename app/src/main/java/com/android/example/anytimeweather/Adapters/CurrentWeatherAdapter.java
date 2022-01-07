package com.android.example.anytimeweather.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.anytimeweather.Models.CurrentWeather;
import com.android.example.anytimeweather.R;
import com.bumptech.glide.Glide;

import java.sql.Time;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class CurrentWeatherAdapter extends RecyclerView.Adapter<CurrentWeatherAdapter.CurrentWeatherViewHolder> {
    Context mContext;
    ArrayList<CurrentWeather> mCurrentWeatherArrayList;
    private SharedPreferences sharedPreferences;
    private int lastPos = -1;

    final private CurrentLocationClicked mCurrentLocationClicked;

    public interface CurrentLocationClicked {
        void onClick(double lat, double lon, String name);
    }

    public CurrentWeatherAdapter(Context context, ArrayList<CurrentWeather> currentWeatherArrayList, CurrentLocationClicked clicked) {
        this.mContext = context;
        this.mCurrentWeatherArrayList = currentWeatherArrayList;
        this.mCurrentLocationClicked = clicked;
    }

    @NonNull
    @Override
    public CurrentWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate =  LayoutInflater.from(mContext).inflate(R.layout.list_item_current_weather, parent, false);
        return new CurrentWeatherViewHolder(inflate);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull CurrentWeatherViewHolder holder, int position) {
        sharedPreferences = mContext.getSharedPreferences("prefWid", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        CurrentWeather currentWeather = mCurrentWeatherArrayList.get(position);

        Glide.with(mContext).load(currentWeather.getCurrentIcon()).into(holder.mIcon);
        editor.putString("icon", currentWeather.getCurrentIcon());

        holder.mCapital.setText( currentWeather.getCurrentCapital() );
        holder.mCapital.setSelected(true); //If the capital name is bigger than the size of the tv then the tv goes from right to left
        editor.putString("cap", currentWeather.getCurrentCapital());

        holder.mCountry.setText( currentWeather.getCurrentCountry() );
        editor.putString("cou", currentWeather.getCurrentCountry());

        holder.setLocalDateAndTime( currentWeather.getCurrentLocalTimeAndDate() );

        SharedPreferences sharedPreferences1 = mContext.getSharedPreferences("prefTempUnits", MODE_PRIVATE);
        if(sharedPreferences1.getString("unit", "Celsius").equals("Celsius")){
            holder.mCel.setText(Double.toString(currentWeather.getCurrentCel()));
            editor.putString("temp", Double.toString(currentWeather.getCurrentCel()));

            holder.mFeels.setText( Double.toString(currentWeather.getCurrentFeelsCel()) );
            editor.putString("feels", Double.toString(currentWeather.getCurrentFeelsCel()));
        } else {
            holder.mCel.setText(Double.toString(currentWeather.getCurrentFeh()));
            editor.putString("temp", Double.toString(currentWeather.getCurrentFeh()));

            holder.mFeels.setText( Double.toString(currentWeather.getCurrentFeelsFeh()) );
            editor.putString("feels", Double.toString(currentWeather.getCurrentFeelsFeh()));
        }

        holder.mWindSpeed.setText( Double.toString(currentWeather.getCurrentWindSpeed()) );
        holder.mWindDir.setText( currentWeather.getCurrentWindDirection() );
        holder.mPressure.setText( Double.toString(currentWeather.getCurrentPressure()) );
        holder.mPrecipitation.setText( Double.toString(currentWeather.getCurrentPrecipitation()) );
        holder.mHumidity.setText( Integer.toString(currentWeather.getCurrentHumidity()) );
        holder.mCloud.setText( Integer.toString(currentWeather.getCurrentClouds()) );


        holder.mUv.setText( Double.toString(currentWeather.getCurrentUv()) );
        editor.putString("uv", Double.toString(currentWeather.getCurrentUv()));

        holder.mP25.setText( String.format("%.2f", currentWeather.getCurrentPm25()) );
        holder.mP10.setText( String.format("%.2f", currentWeather.getCurrentPm10()) );
        holder.setIndex( currentWeather.getCurrentIndex() );

        holder.setUpdatedDateAndTime( currentWeather.getCurrentUpdatedDateAndTime() );
        editor.apply();

        holder.mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentLocationClicked.onClick(
                        currentWeather.getCurrentLat(),
                        currentWeather.getCurrentLon(),
                        currentWeather.getCurrentCapital());
            }
        });

        if( holder.getAdapterPosition() > lastPos ){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_up);
            holder.itemView.setAnimation(animation);
            lastPos = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mCurrentWeatherArrayList.size();
    }

    class CurrentWeatherViewHolder extends RecyclerView.ViewHolder {

        private TextView mCapital, mCountry, mUpdatedDate, mUpdatedTime, mLocalDate, mLocalTime, mCel, mWindSpeed, mWindDir, mPressure, mPrecipitation;
        private TextView mHumidity, mCloud, mFeels, mUv, mP25, mP10, mIndex, mLocation;
        private ImageView mIcon;

        public CurrentWeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            mIcon = itemView.findViewById(R.id.current_weather_condition_icon);

            mCapital = itemView.findViewById(R.id.current_weather_capital);
            mCountry = itemView.findViewById(R.id.current_weather_country);
            mLocalDate = itemView.findViewById(R.id.local_date);
            mLocalTime = itemView.findViewById(R.id.local_time);
            mCel = itemView.findViewById(R.id.temp_1);
            mWindSpeed = itemView.findViewById(R.id.wind_speed_val);
            mWindDir = itemView.findViewById(R.id.wind_dir_val);
            mPressure = itemView.findViewById(R.id.pressure_val);
            mPrecipitation = itemView.findViewById(R.id.precipitation_val);
            mHumidity = itemView.findViewById(R.id.humidity_val);
            mCloud = itemView.findViewById(R.id.cloud_val);
            mFeels = itemView.findViewById(R.id.temp_2);
            mUv = itemView.findViewById(R.id.uv_index);

            mP25 = itemView.findViewById(R.id.pm_2_5_val);
            mP10 = itemView.findViewById(R.id.pm_10_val);
            mIndex = itemView.findViewById(R.id.us_index_val);

            mUpdatedDate = itemView.findViewById(R.id.updated_date);
            mUpdatedTime = itemView.findViewById(R.id.updated_time);

            mLocation = itemView.findViewById(R.id.current_weather_location);
        }

        //2021-11-19 15:45
        @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
        public void setLocalDateAndTime(String dateAndTime) {

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

            sharedPreferences = mContext.getSharedPreferences("prefWid", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("date", actualDate);
            editor.putString("time", actualTime);
            editor.apply();
        }

        public void setUpdatedDateAndTime(String dateAndTime){

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
