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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.anytimeweather.Models.ForecastWeatherHourly;
import com.android.example.anytimeweather.Models.ForecastWeatherToday;
import com.android.example.anytimeweather.R;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ForecastWeatherTodayAdapter extends RecyclerView.Adapter<ForecastWeatherTodayAdapter.ForecastTodayViewHolder> {

    private Context mContext;
    private ArrayList<ForecastWeatherToday> mForecastTodayArrayList;
    private int lPos = -1;

    public ForecastWeatherTodayAdapter(Context context, ArrayList<ForecastWeatherToday> forecastTodayArrayList) {
        this.mContext = context;
        this.mForecastTodayArrayList = forecastTodayArrayList;
    }

    @NonNull
    @Override
    public ForecastTodayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_forecast_today, parent, false);
        return new ForecastTodayViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ForecastTodayViewHolder holder, int position) {
        ForecastWeatherToday currentItem = mForecastTodayArrayList.get(position);

        Glide.with(mContext).load(currentItem.getIcon()).into(holder.mIcon);

        holder.mCapital.setText( currentItem.getCapital() );
        holder.mCapital.setSelected(true);

        holder.mCountry.setText( currentItem.getCountry() );
        holder.setDateAndTime( currentItem.getUpdatedTimeAndDate() );

        SharedPreferences sharedPreferences1 = mContext.getSharedPreferences("prefTempUnits", MODE_PRIVATE);
        if(sharedPreferences1.getString("unit", "Celsius").equals("Celsius")){
            holder.mMaxTemp.setText( Double.toString(currentItem.getMaxCel()) );
            holder.mMinTemp.setText( Double.toString(currentItem.getMinCel()) );
        } else {
            holder.mMaxTemp.setText( Double.toString(currentItem.getMaxFeh()) );
            holder.mMinTemp.setText( Double.toString(currentItem.getMinFeh()) );
        }

        holder.mHumidity.setText( Double.toString(currentItem.getHumidity()) );
        holder.mPrecipitation.setText( Double.toString(currentItem.getPrecipitation()) );
        holder.mRainChance.setText( "" + currentItem.getRainChance() );
        holder.mSnowChance.setText( "" + currentItem.getSnowChance() );

        holder.mSunrise.setText( currentItem.getSunrise() );
        holder.mSunset.setText( currentItem.getSunset() );
        holder.mMoonrise.setText( currentItem.getMoonrise() );
        holder.mMoonSet.setText( currentItem.getMoonSet() );

        ArrayList<ForecastWeatherHourly> hourlyItems = currentItem.getWeatherHourlyArrayList();
        ForecastWeatherHourlyAdapter weatherHourlyAdapter = new ForecastWeatherHourlyAdapter(mContext, hourlyItems);
        holder.mRecyclerView.setHasFixedSize(true);
        holder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.mRecyclerView.setAdapter(weatherHourlyAdapter);

        if( lPos < holder.getAdapterPosition()){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_up);
            holder.itemView.setAnimation(animation);
            lPos = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mForecastTodayArrayList.size();
    }

    public class ForecastTodayViewHolder extends RecyclerView.ViewHolder{

        private TextView mCapital, mCountry, mUpdatedDate, mUpdatedTime, mSunrise, mSunset, mMoonrise, mMoonSet;
        private TextView mMaxTemp, mMinTemp, mHumidity, mPrecipitation, mRainChance, mSnowChance;
        private ImageView mIcon;
        private RecyclerView mRecyclerView;

        public ForecastTodayViewHolder(@NonNull View itemView) {
            super(itemView);

            mCapital = itemView.findViewById(R.id.forecast_today_capital);
            mCountry = itemView.findViewById(R.id.forecast_today_country);
            mUpdatedDate = itemView.findViewById(R.id.tv_updated_date);
            mUpdatedTime = itemView.findViewById(R.id.tv_updated_time);
            mIcon = itemView.findViewById(R.id.forecast_today_iv);
            mMaxTemp = itemView.findViewById(R.id.forecast_today_max_temp);
            mMinTemp = itemView.findViewById(R.id.forecast_today_min_temp);

            mHumidity = itemView.findViewById(R.id.tv_humidity_val);
            mPrecipitation = itemView.findViewById(R.id.tv_precipitation_val);
            mRainChance = itemView.findViewById(R.id.tv_rain_val);
            mSnowChance = itemView.findViewById(R.id.tv_snow_val);

            mSunrise = itemView.findViewById(R.id.tv_sunrise_val);
            mSunset = itemView.findViewById(R.id.tv_sunset_val);
            mMoonrise = itemView.findViewById(R.id.tv_moonrise_val);
            mMoonSet = itemView.findViewById(R.id.tv_moonset_val);

            mRecyclerView = itemView.findViewById(R.id.forecast_rv_horizontal);
        }

        @SuppressLint("SimpleDateFormat")
        private void setDateAndTime(String dateAndTime){

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

            mUpdatedDate.setText(weekName + ", " + actualDate);
            mUpdatedTime.setText(actualTime);
        }
    }
}
