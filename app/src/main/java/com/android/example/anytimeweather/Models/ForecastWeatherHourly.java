package com.android.example.anytimeweather.Models;

public class ForecastWeatherHourly {

    private String mIcon, mTime;
    private double mCel, mFeh;
    private int mRain, mSnow;

    public ForecastWeatherHourly(String icon, String time, double cel, double feh, int rain, int snow){
        mIcon = icon;
        mTime = time;
        mCel = cel;
        mFeh = feh;
        mRain = rain;
        mSnow = snow;
    }

    public String getHourlyIcon() {
        return mIcon;
    }

    public String getHourlyTime() {
        return mTime;
    }

    public double getHourlyCel() {
        return mCel;
    }

    public double getHourlyFeh() {
        return mFeh;
    }

    public int getHourlyRain() {
        return mRain;
    }

    public int getHourlySnow() {
        return mSnow;
    }
}
