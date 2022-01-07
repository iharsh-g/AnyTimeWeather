package com.android.example.anytimeweather.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = REPLACE)
    void insert(FavoriteData favoriteData);

    @Query("DELETE FROM table_name WHERE locationId = :locId")
    void delete(int locId);

    //Delete all data
    @Delete
    void reset(List<FavoriteData> favoriteData);

    //Get all data
    @Query("SELECT * FROM table_name")
    List<FavoriteData> getAll();

    @Query("SELECT EXISTS(SELECT * FROM table_name WHERE locationId = :locId)")
    boolean isRowIsExist(int locId);
}
