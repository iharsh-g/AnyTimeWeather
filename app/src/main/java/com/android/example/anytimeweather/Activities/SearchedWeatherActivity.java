package com.android.example.anytimeweather.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.example.anytimeweather.Adapters.SearchWeatherAdapter;
import com.android.example.anytimeweather.Database.FavoriteData;
import com.android.example.anytimeweather.Database.RoomDB;
import com.android.example.anytimeweather.Models.SearchWeather;
import com.android.example.anytimeweather.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchedWeatherActivity extends AppCompatActivity {

    private ArrayList<SearchWeather> mList;
    private RequestQueue mQueue;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private SearchWeatherAdapter mAdapter;

    private RoomDB mDatabase;
    private String mCap, mCountry;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_weather);

        mDatabase = RoomDB.getDatabase(this);
        mList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_search_weather);
        mProgressBar = findViewById(R.id.pb_search_weather);
        fab = findViewById(R.id.fab_searched_weather);

        if(isInFav()){
            fab.setImageResource(R.drawable.ic_without_border_favorite);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_border);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInFav()){
                    remFav();
                } else {
                    addFav();
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SearchWeatherAdapter(this, mList);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mQueue = Volley.newRequestQueue(this);

        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        jsonParse();
    }

    private void jsonParse(){
        String url = "https://api.weatherapi.com/v1/current.json?key=730b153a05d74989aef61637211811&q=" + getIntent().getStringExtra("name") + "&aqi=yes";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONObject jsonObject = response.getJSONObject("location");
                            mCap = jsonObject.getString("name");
                            mCountry = jsonObject.getString("country");
                            String local_date_and_time = jsonObject.getString("localtime");

                            JSONObject currentJsonObject = response.getJSONObject("current");
                            String updated_date_and_time = currentJsonObject.getString("last_updated");
                            double cel = currentJsonObject.getDouble("temp_c");
                            double feh = currentJsonObject.getDouble("temp_f");
                            int humidity = currentJsonObject.getInt("humidity");
                            int cloud = currentJsonObject.getInt("cloud");
                            double feels_like_c = currentJsonObject.getDouble("feelslike_c");
                            double feels_like_f = currentJsonObject.getDouble("feelslike_f");
                            double uv = currentJsonObject.getDouble("uv");

                            JSONObject iconOb = currentJsonObject.getJSONObject("condition");
                            String icon = "https:" + iconOb.getString("icon");

                            JSONObject aqiOb = currentJsonObject.getJSONObject("air_quality");
                            int index = aqiOb.getInt("us-epa-index");

                            mProgressBar.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            SearchWeather searchWeather = new SearchWeather
                                    (mCap, mCountry, local_date_and_time, updated_date_and_time, icon,
                                     cel, feh, feels_like_c, feels_like_f, uv, humidity, cloud, index);
                            mList.add(searchWeather);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);
    }

    boolean isInFav(){
        return mDatabase.favoriteDao().isRowIsExist(getIntent().getExtras().getInt("locId"));
    }

    void addFav(){
        FavoriteData data = new FavoriteData();
        data.setLocationId(getIntent().getExtras().getInt("locId"));
        Log.e("SWA ", "" + getIntent().getExtras().getInt("locId"));
        data.setCapital(mCap);
        data.setCountry(mCountry);
        mDatabase.favoriteDao().insert(data);

        fab.setImageResource(R.drawable.ic_without_border_favorite);
        Snackbar.make(fab, "Added to Favourites", Snackbar.LENGTH_SHORT).show();
    }

    void remFav(){
        SharedPreferences sharedPreferences1 = getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);

        if(sharedPreferences1.getInt("locId", -1) == getIntent().getExtras().getInt("locId")){
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString("capitalName", "");
            editor.putString("countryName", "");
            editor.putInt("locId", -1);
            editor.apply();
        }

        mDatabase.favoriteDao().delete(getIntent().getExtras().getInt("locId"));
        fab.setImageResource(R.drawable.ic_favorite_border);
        Snackbar.make(fab, "Removed from Favourites", Snackbar.LENGTH_SHORT).show();
    }
}