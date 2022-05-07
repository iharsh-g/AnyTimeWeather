package com.android.example.anytimeweather.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.example.anytimeweather.Adapters.ForecastWeatherTodayAdapter;
import com.android.example.anytimeweather.Models.ForecastWeatherHourly;
import com.android.example.anytimeweather.Models.ForecastWeatherToday;
import com.android.example.anytimeweather.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForecastWeatherFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public ForecastWeatherFragment(){    }

    private int j = 0;
    private SwipeRefreshLayout mSwipe;
    private ArrayList<ForecastWeatherToday> mForecastList;
    private RequestQueue mQueue;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ForecastWeatherTodayAdapter mForecastWeatherTodayAdapter;

    private SharedPreferences sharedPreferences, sharedPreferences2;

    private TextView mEmptyTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_forecast_weather, container, false);

        sharedPreferences = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        sharedPreferences2 = getContext().getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);

        mEmptyTv = view.findViewById(R.id.emptyTV);

        mForecastList = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.rv_forecast);
        mProgressBar = view.findViewById(R.id.pb_forecast);
        mSwipe = view.findViewById(R.id.refresh_forecast);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        mForecastWeatherTodayAdapter = new ForecastWeatherTodayAdapter(getContext(), mForecastList);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mForecastWeatherTodayAdapter);

        mQueue = Volley.newRequestQueue(requireContext());

        Button button = view.findViewById(R.id.frag_forecast_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);
                View sheet = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_dialog, null);

                sheet.findViewById(R.id.bottom_sheet_tv_1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Today's Weather", Toast.LENGTH_SHORT).show();
                        j = 0;
                        mRecyclerView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mForecastList.clear();
                        mForecastWeatherTodayAdapter.notifyDataSetChanged();
                        jsonParse();
                        button.setText("Today's Weather");
                        bottomSheetDialog.cancel();
                    }
                });

                sheet.findViewById(R.id.bottom_sheet_tv_2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Tomorrow Weather", Toast.LENGTH_SHORT).show();
                        j = 1;
                        mRecyclerView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mForecastList.clear();
                        mForecastWeatherTodayAdapter.notifyDataSetChanged();
                        jsonParse();
                        button.setText("Tomorrow's Weather");
                        bottomSheetDialog.cancel();
                    }
                });

                sheet.findViewById(R.id.bottom_sheet_tv_3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Day After Tomorrow Weather", Toast.LENGTH_SHORT).show();
                        j = 2;
                        mRecyclerView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mForecastList.clear();
                        mForecastWeatherTodayAdapter.notifyDataSetChanged();
                        jsonParse();
                        button.setText("Day After Tomorrow's Weather");
                        bottomSheetDialog.cancel();
                    }
                });

                sheet.findViewById(R.id.bottom_sheet_tv_4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });

                bottomSheetDialog.setContentView(sheet);
                bottomSheetDialog.show();
            }
        });

        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        mSwipe.setOnRefreshListener(this);
        return view;
    }

    private void jsonParse(){
        String url;

        if(!sharedPreferences2.getString("capitalName", "").equals("") && !sharedPreferences2.getString("countryName", "").equals("")){
            url = "https://api.weatherapi.com/v1/forecast.json?key=730b153a05d74989aef61637211811&q=" + sharedPreferences2.getString("capitalName", "") + " "
                    + sharedPreferences2.getString("countryName", "") + "&days=7&aqi=yes&alerts=no";
        } else {
            url = "https://api.weatherapi.com/v1/forecast.json?key=730b153a05d74989aef61637211811&q=" + sharedPreferences.getString("gpsLat", "") + " "
                    + sharedPreferences.getString("gpsLon", "") + "&days=7&aqi=yes&alerts=no";
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("location");
                            String capital = jsonObject.getString("name");
                            String country = jsonObject.getString("country");

                            JSONObject current = response.getJSONObject("current");
                            String updated_dataAndTime = current.getString("last_updated");

                            JSONObject forecast = response.getJSONObject("forecast");
                            JSONArray forecastDay = forecast.getJSONArray("forecastday");

                            double maxCel = 0, minCel = 0, maxFeh = 0, minFeh = 0, precipitation = 0, humidity = 0 ;
                            int rain = 0, snow = 0;
                            String icon = null, sunrise = null, sunset = null, moonrise = null, moonSet = null;

                            ArrayList<ForecastWeatherHourly> arrayList = new ArrayList<>();
                            for(int i=0;i<=forecastDay.length();i++){
                                Log.e("FWF", "aa rha h");
                                if(i == j){
                                    JSONObject currentDay = forecastDay.getJSONObject(j);
                                    JSONObject currentDayOb = currentDay.getJSONObject("day");
                                    maxCel = currentDayOb.getDouble("maxtemp_c");
                                    maxFeh = currentDayOb.getDouble("maxtemp_f");
                                    minCel = currentDayOb.getDouble("mintemp_c");
                                    minFeh = currentDayOb.getDouble("mintemp_f");
                                    precipitation = currentDayOb.getDouble("totalprecip_mm");
                                    humidity = currentDayOb.getDouble("avghumidity");
                                    rain = currentDayOb.getInt("daily_chance_of_rain");
                                    snow = currentDayOb.getInt("daily_chance_of_snow");

                                    JSONObject con = currentDayOb.getJSONObject("condition");
                                    icon = "https:" + con.getString("icon");

                                    JSONObject currentAstro = currentDay.getJSONObject("astro");
                                    sunrise = currentAstro.getString("sunrise");
                                    sunset = currentAstro.getString("sunset");
                                    moonrise = currentAstro.getString("moonrise");
                                    moonSet = currentAstro.getString("moonset");

                                    JSONArray hour = currentDay.getJSONArray("hour");
                                    for(int k = 0; k < hour.length(); k++){
                                        JSONObject currentHour = hour.getJSONObject(k);
                                        String time = currentHour.getString("time");
                                        double cel = currentHour.getDouble("temp_c");
                                        double feh = currentHour.getDouble("temp_f");
                                        int rain2 = currentHour.getInt("chance_of_rain");
                                        int snow2 = currentHour.getInt("chance_of_snow");

                                        JSONObject object = currentHour.getJSONObject("condition");
                                        String icon2 = "https:" + object.getString("icon");

                                        ForecastWeatherHourly weatherHourly = new ForecastWeatherHourly(icon2, time, cel, feh, rain2, snow2);
                                        arrayList.add(weatherHourly);
                                    }
                                }
                            }

                            mProgressBar.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mEmptyTv.setVisibility(View.GONE);
                            mSwipe.setRefreshing(false);
                            ForecastWeatherToday forecastToday = new ForecastWeatherToday(capital, country, updated_dataAndTime, icon, sunrise, sunset, moonrise, moonSet,
                                    maxCel, minCel, maxFeh, minFeh, precipitation,
                                    humidity, rain, snow, arrayList);

                            mForecastList.add(forecastToday);

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
        mForecastList.clear();
        mForecastWeatherTodayAdapter.notifyDataSetChanged();

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
        mForecastList.clear();
        mForecastWeatherTodayAdapter.notifyDataSetChanged();

        if(!sharedPreferences.getString("gpsLat", "").equals("")) {
            jsonParse();
        }
        else {
            mEmptyTv.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mSwipe.setRefreshing(false);
        }
    }
}
