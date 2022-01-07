package com.android.example.anytimeweather.Models;

public class SearchWeather {
    private String mCapital, mCountry, mLocalTimeAndDate, mUpdatedTimeAndDate, mIcon;
    private double mCel, mFeh, mCelFeels, mFehFeels, mUv;
    private int mIndex, mHumidity, mCloud;

    public SearchWeather(String capital, String country, String localTimeAndDate, String updatedTimeAndDate, String icon,
                        double cel, double feh, double celFeels, double fehFeels, double uv, int humidity, int cloud, int index){
        this.mCapital = capital;
        this.mCountry = country;
        this.mLocalTimeAndDate = localTimeAndDate;
        this.mUpdatedTimeAndDate = updatedTimeAndDate;
        this.mIcon = icon;
        this.mCel = cel;
        this.mFeh = feh;
        this.mCelFeels = celFeels;
        this.mFehFeels = fehFeels;
        this.mUv = uv;
        this.mHumidity = humidity;
        this.mCloud = cloud;
        this.mIndex = index;
    }

    public String getSearchWeatherCapital() {
        return mCapital;
    }

    public String getSearchWeatherCountry() {
        return mCountry;
    }

    public String getSearchWeatherLocalTimeAndDate() {
        return mLocalTimeAndDate;
    }

    public String getSearchWeatherUpdatedTimeAndDate() {
        return mUpdatedTimeAndDate;
    }

    public String getSearchWeatherIcon(){
        return mIcon;
    }

    public double getSearchWeatherCel() {
        return mCel;
    }

    public double getSearchWeatherFeh() {
        return mFeh;
    }

    public double getSearchWeatherCelFeels() {
        return mCelFeels;
    }

    public double getSearchWeatherFehFeels() {
        return mFehFeels;
    }

    public double getSearchWeatherUv() {
        return mUv;
    }

    public int getSearchWeatherHumidity() {
        return mHumidity;
    }

    public int getSearchWeatherCloud() {
        return mCloud;
    }

    public int getSearchWeatherIndex() {
        return mIndex;
    }
}
