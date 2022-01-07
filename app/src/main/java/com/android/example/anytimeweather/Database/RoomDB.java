package com.android.example.anytimeweather.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Database Entities
@Database(entities = {FavoriteData.class}, version = 4, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {

    //databaseInstance
    private static RoomDB database;

    //database name
    private static String DATABASE_NAME = "favorite_db";

    public synchronized static RoomDB getDatabase(Context context){

        if(database == null){
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    //Create Dao
    public  abstract FavoriteDao favoriteDao();
}
