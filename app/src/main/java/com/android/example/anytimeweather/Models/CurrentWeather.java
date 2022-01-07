package com.android.example.anytimeweather.Models;

public class CurrentWeather {

    private String mCapital, mCountry, mLocalTimeAndDate, mUpdatedTimeAndDate, mWindDirection, mIcon;
    private double mCel, mFeh, mWindSpeed, mPressure, mPrecipitation, mFeelsC, mFeelsF, mUv, mPm25, mPm10;
    private int mHumidity, mClouds, mIndex;
    private double mLat, mLon;

    public CurrentWeather(String capital, String country, String localTimeAndDate, String updatedTimeAndDate, String windDirection, String icon,
                          double cel, double feh, double windSpeed, double pressure, double precipitation, double feelsC, double feelsF,double uv, double pm25, double pm10,
                          int humidity, int clouds, int index, double lat, double lon)
    {
        this.mCapital = capital; this.mCountry = country; this.mLocalTimeAndDate = localTimeAndDate;
        this.mUpdatedTimeAndDate = updatedTimeAndDate; this.mWindDirection = windDirection;
        this.mIcon = icon; this.mCel = cel; this.mFeh = feh; this.mWindSpeed = windSpeed; this.mPressure = pressure;
        this.mPrecipitation = precipitation; this.mFeelsC = feelsC; this.mFeelsF = feelsF; this.mUv = uv; this.mPm25 = pm25;
        this.mPm10 = pm10; this.mHumidity = humidity; this.mClouds = clouds; this.mIndex = index;
        this.mLat = lat; this.mLon = lon;
    }

    public String getCurrentCapital() {
        return mCapital;
    }

    public String getCurrentCountry(){
        return mCountry;
    }

    public String getCurrentLocalTimeAndDate() {
        return mLocalTimeAndDate;
    }

    public String getCurrentUpdatedDateAndTime() {
        return mUpdatedTimeAndDate;
    }

    public String getCurrentWindDirection() {
        return mWindDirection;
    }

    public String getCurrentIcon() {
        return mIcon;
    }

    public double getCurrentCel() {
        return mCel;
    }

    public double getCurrentFeh() {
        return mFeh;
    }

    public double getCurrentWindSpeed() {
        return mWindSpeed;
    }

    public double getCurrentPressure() {
        return mPressure;
    }

    public double getCurrentPrecipitation() {
        return mPrecipitation;
    }

    public double getCurrentFeelsCel() {
        return mFeelsC;
    }

    public double getCurrentFeelsFeh() {
        return mFeelsF;
    }

    public double getCurrentUv() {
        return mUv;
    }

    public double getCurrentPm25() {
        return mPm25;
    }

    public double getCurrentPm10() {
        return mPm10;
    }

    public int getCurrentHumidity() {
        return mHumidity;
    }

    public int getCurrentClouds() {
        return mClouds;
    }

    public int getCurrentIndex() {
        return mIndex;
    }

    public double getCurrentLat() {
        return mLat;
    }

    public double getCurrentLon() {
        return mLon;
    }
}
