package com.android.example.anytimeweather.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.example.anytimeweather.Adapters.CurrentWeatherAdapter;
import com.android.example.anytimeweather.Models.CurrentWeather;
import com.android.example.anytimeweather.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class CurrentWeatherFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CurrentWeatherAdapter.CurrentLocationClicked {

    public CurrentWeatherFragment() {
    }

    private SwipeRefreshLayout mSwipe;
    private ArrayList<CurrentWeather> mCurrentList;
    private RequestQueue mQueue;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private CurrentWeatherAdapter mCurrentWeatherAdapter;

    private SharedPreferences sharedPreferences, sharedPreferences2;

    private TextView mEmptyTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);

        sharedPreferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        sharedPreferences2 = requireContext().getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);

        mCurrentList = new ArrayList<>();
        mEmptyTv = view.findViewById(R.id.emptyTV);
        mProgressBar = view.findViewById(R.id.pb_current);
        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.refresh_current);

        mRecyclerView = view.findViewById(R.id.rv_current);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        mCurrentWeatherAdapter = new CurrentWeatherAdapter(getContext(), mCurrentList, this);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mCurrentWeatherAdapter);

        mQueue = Volley.newRequestQueue(requireContext());

        mSwipe.setOnRefreshListener(this);

        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        return view;
    }

    void jsonParse(){
        String url;
        if(!sharedPreferences2.getString("capitalName", "").equals("") && !sharedPreferences2.getString("countryName", "").equals("")){
            url = "https://api.weatherapi.com/v1/current.json?key=730b153a05d74989aef61637211811&q=" + sharedPreferences2.getString("capitalName", "") + " "
                    + sharedPreferences2.getString("countryName", "") + "&aqi=yes";

            Log.e("MainActivityJsonUrl", "" + url);
            Log.e("MainActivityJson", "" + sharedPreferences2.getString("capitalName", ""));
            Log.e("MainActivityJson", "" + sharedPreferences2.getString("countryName", ""));
        } else {
            url = "https://api.weatherapi.com/v1/current.json?key=730b153a05d74989aef61637211811&q=" + sharedPreferences.getString("gpsLat", "") + " "
                    + sharedPreferences.getString("gpsLon", "") + "&aqi=yes";

            Log.e("MainActivityJsonUrl", "" + url);
            Log.e("MainActivityJson", "" + sharedPreferences.getString("gpsLat", ""));
            Log.e("MainActivityJson", "" + sharedPreferences.getString("gpsLon", ""));
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("location");
                            String capital = jsonObject.getString("name");
                            String country = jsonObject.getString("country");
                            double lat = jsonObject.getDouble("lat");
                            double lon = jsonObject.getDouble("lon");
                            String local_date_and_time = jsonObject.getString("localtime");

                            JSONObject currentJsonObject = response.getJSONObject("current");
                            String updated_date_and_time = currentJsonObject.getString("last_updated");
                            double cel = currentJsonObject.getDouble("temp_c");
                            double feh = currentJsonObject.getDouble("temp_f");
                            double wind_speed = currentJsonObject.getDouble("wind_kph");
                            String wind_dir = currentJsonObject.getString("wind_dir");
                            double pressure = currentJsonObject.getDouble("pressure_mb");
                            double precipitation = currentJsonObject.getDouble("precip_mm");
                            int humidity = currentJsonObject.getInt("humidity");
                            int cloud = currentJsonObject.getInt("cloud");
                            double feels_likeC = currentJsonObject.getDouble("feelslike_c");
                            double feels_likeF = currentJsonObject.getDouble("feelslike_f");
                            double uv = currentJsonObject.getDouble("uv");

                            JSONObject conditionJsonObject = currentJsonObject.getJSONObject("condition");
                            String icon = "https:" + conditionJsonObject.getString("icon");

                            JSONObject airJsonObject = currentJsonObject.getJSONObject("air_quality");
                            double pm2_5 = airJsonObject.getDouble("pm2_5");
                            double pm10 = airJsonObject.getDouble("pm10");
                            int index = airJsonObject.getInt("us-epa-index");

                            mProgressBar.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mEmptyTv.setVisibility(View.GONE);
                            mSwipe.setRefreshing(false);
                            CurrentWeather currentWeather = new CurrentWeather(
                                    capital, country, local_date_and_time, updated_date_and_time, wind_dir, icon,
                                    cel, feh, wind_speed, pressure, precipitation, feels_likeC, feels_likeF, uv,
                                                pm2_5, pm10, humidity, cloud, index, lat, lon);
                            mCurrentList.add(currentWeather);
                        } catch (JSONException e) {
                            mEmptyTv.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mEmptyTv.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        mQueue.add(request);
    }

    @Override
    public void onRefresh() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyTv.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mCurrentList.clear();
        mCurrentWeatherAdapter.notifyDataSetChanged();

        if(!sharedPreferences.getString("gpsLat", "").equals("")) {
            jsonParse();
        }
        else {
            mEmptyTv.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mSwipe.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setVisibility(View.GONE);
        mEmptyTv.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mCurrentList.clear();
        mCurrentWeatherAdapter.notifyDataSetChanged();

        if(!sharedPreferences.getString("gpsLat", "").equals("")) {
            jsonParse();
        }
        else {
            mEmptyTv.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mSwipe.setRefreshing(false);
        }
    }

    @Override
    public void onClick(double lat, double lon, String name) {

        if(!sharedPreferences2.getString("capitalName", "").equals("") && !sharedPreferences2.getString("countryName", "").equals("")){
            Intent intent = new Intent(getContext(), Map.class);
            intent.putExtra("lat", lat);
            intent.putExtra("long", lon);
            intent.putExtra("name", name);
            startActivity(intent);

            Log.e("MainActivityJson", "" + sharedPreferences2.getString("capitalName", ""));
            Log.e("MainActivityJson", "" + sharedPreferences2.getString("countryName", ""));
        } else {

            Intent intent = new Intent(getContext(), Map.class);
            intent.putExtra("lat", sharedPreferences.getString("gpsLat", ""));
            intent.putExtra("long", sharedPreferences.getString("gpsLon", ""));
            intent.putExtra("name", sharedPreferences.getString("gpsCapitalName", "Not, location, Available"));
            startActivity(intent);

            Log.e("MainActivityJson", "" + sharedPreferences.getString("gpsLat", ""));
            Log.e("MainActivityJson", "" + sharedPreferences.getString("gpsLon", ""));
        }
    }
}
