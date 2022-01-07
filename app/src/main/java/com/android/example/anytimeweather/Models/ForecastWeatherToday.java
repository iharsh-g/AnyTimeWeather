package com.android.example.anytimeweather.Models;

import java.util.ArrayList;

public class ForecastWeatherToday {

    private String mCapital, mCountry, mUpdatedTimeAndDate, mIcon, mSunrise, mSunset, mMoonrise, mMoonSet;
    private double mMaxCel, mMinCel, mMaxFeh, mMinFeh, mPrecipitation, mHumidity;
    private int mRainChance, mSnowChance;
    private ArrayList<ForecastWeatherHourly> weatherHourlyArrayList;

    public ForecastWeatherToday(String capital, String country, String updatedTimeAndDate, String icon, String sunrise, String sunset, String moonrise, String moonSet,
                                double maxCel, double minCel, double maxFeh, double minFeh, double precipitation,
                                double humidity, int rainChance, int snowChance, ArrayList<ForecastWeatherHourly> arrayList) {
        this.mCapital = capital; this.mCountry = country; this.mUpdatedTimeAndDate = updatedTimeAndDate; this.mIcon = icon;
        this.mSunrise = sunrise; this.mSunset = sunset; this.mMoonrise = moonrise; this.mMoonSet = moonSet;
        this.mMaxCel = maxCel; this.mMinCel = minCel; this.mMaxFeh = maxFeh; this.mMinFeh = minFeh;
        this.mPrecipitation = precipitation; this.mHumidity = humidity; this.mRainChance = rainChance; this.mSnowChance = snowChance;
        this.weatherHourlyArrayList = arrayList;
    }

    public String getCapital() {
        return mCapital;
    }

    public String getCountry() {
        return mCountry;
    }

    public String getUpdatedTimeAndDate() {
        return mUpdatedTimeAndDate;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getSunrise() {
        return mSunrise;
    }

    public String getSunset() {
        return mSunset;
    }

    public String getMoonrise() {
        return mMoonrise;
    }

    public String getMoonSet() {
        return mMoonSet;
    }

    public double getMaxCel() {
        return mMaxCel;
    }

    public double getMinCel() {
        return mMinCel;
    }

    public double getMaxFeh() {
        return mMaxFeh;
    }

    public double getMinFeh() {
        return mMinFeh;
    }

    public double getPrecipitation() {
        return mPrecipitation;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public int getRainChance() {
        return mRainChance;
    }

    public int getSnowChance() {
        return mSnowChance;
    }

    public ArrayList<ForecastWeatherHourly> getWeatherHourlyArrayList() {
        return weatherHourlyArrayList;
    }
}
