package com.android.example.anytimeweather.Models;

public class SearchLocation {

    private double mLat, mLon;
    private String mName, mCountry, mCapital;
    private int locId;

    public SearchLocation(int id, String name, String country, String capital, double lat, double lon){
        this.locId = id;
        this.mName = name;
        this.mCapital = capital;
        this.mCountry = country;
        this.mLat = lat;
        this.mLon = lon;
    }

    public int getLocId() {
        return locId;
    }

    public double getSearchLat() {
        return mLat;
    }

    public double getSearchLon() {
        return mLon;
    }

    public String getSearchName() {
        return mName;
    }

    public String getSearchCountry() {
        return mCountry;
    }

    public String getSearchCapital() {
        return mCapital;
    }
}
