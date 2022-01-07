package com.android.example.anytimeweather.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//Define table name
@Entity(tableName = "table_name")
public class FavoriteData implements Serializable {

    //id_Col
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    private int ID;

    @ColumnInfo(name = "locationId")
    private int locationId;

    //text_COl
    @ColumnInfo(name = "Capital")
    private String capital;

    @ColumnInfo(name = "Country")
    private String country;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getLocationId() {
        return ID;
    }

    public void setLocationId(int ID) {
        this.ID = ID;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String cap) {
        this.capital = cap;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
