package com.android.example.anytimeweather.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.example.anytimeweather.Adapters.CurrentWeatherAdapter;
import com.android.example.anytimeweather.Map;
import com.android.example.anytimeweather.Models.CurrentWeather;
import com.android.example.anytimeweather.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CurrentWeatherFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CurrentWeatherAdapter.CurrentLocationClicked {

    public CurrentWeatherFragment() {
    }

    private SwipeRefreshLayout mSwipe;
    private ArrayList<CurrentWeather> mCurrentList;
    private RequestQueue mQueue;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private CurrentWeatherAdapter mCurrentWeatherAdapter;

    private FusedLocationProviderClient client;

    private SharedPreferences sharedPreferences, sharedPreferences2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);

        sharedPreferences = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        sharedPreferences2 = requireContext().getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);

        mCurrentList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.rv_current);
        mProgressBar = view.findViewById(R.id.pb_current);
        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.refresh_current);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        mCurrentWeatherAdapter = new CurrentWeatherAdapter(getContext(), mCurrentList, this);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mCurrentWeatherAdapter);

        mQueue = Volley.newRequestQueue(requireContext());

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        if (sharedPreferences.getString("gpsLat", "").equals("")) {
            if (ContextCompat.checkSelfPermission(getActivity()
                    , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                getCurrentLocation();
            } else {
                //When permission is denied
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }

        mSwipe.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && (grantResults.length > 0) &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            getCurrentLocation();
        } else {
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Initialize Location
                    Location location = task.getResult();
                    if (location != null) {

                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("gpsCapitalName", addresses.get(0).getAdminArea());
                            editor.putString("gpsCountryName", addresses.get(0).getCountryName());
                            Log.e("gpsCapitalName", addresses.get(0).getAdminArea() + "");
                            Log.e("gpsCountryName", "" + addresses.get(0).getCountryName());
                            editor.apply();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("gpsLat", Double.toString(location.getLatitude()));
                        editor.putString("gpsLon", Double.toString(location.getLongitude()));
                        editor.apply();
                        Log.e("gpsLat", location.getLatitude() + "");
                        Log.e("gpsLon", "" + location.getLongitude());

                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(1000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();

                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(
                                            location1.getLatitude(), location1.getLongitude(), 1);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("gpsCapitalName", addresses.get(0).getAdminArea());
                                    editor.putString("gpsCountryName", addresses.get(0).getCountryName());
                                    Log.e("gpsCapitalName", addresses.get(0).getAdminArea() + "");
                                    Log.e("gpsCountryName", "" + addresses.get(0).getCountryName());
                                    editor.apply();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("gpsLat", Double.toString(location1.getLatitude()));
                                editor.putString("gpsLon", Double.toString(location1.getLongitude()));
                                editor.apply();
                                Log.e("gpsLat", location1.getLatitude() + "");
                                Log.e("gpsLon", "" + location1.getLongitude());
                            }
                        };

                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });

            if(sharedPreferences.getString("gpsLat", "").equals("")){
                Toast.makeText(getActivity(), "Swipe down to refresh!", Toast.LENGTH_SHORT).show();
            }
            else {
                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                jsonParse();
            }

        } else {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            getCurrentLocation();
        }
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
                            mSwipe.setRefreshing(false);
                            CurrentWeather currentWeather = new CurrentWeather(
                                    capital, country, local_date_and_time, updated_date_and_time, wind_dir, icon,
                                    cel, feh, wind_speed, pressure, precipitation, feels_likeC, feels_likeF, uv,
                                                pm2_5, pm10, humidity, cloud, index, lat, lon);
                            mCurrentList.add(currentWeather);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    @Override
    public void onRefresh() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mCurrentList.clear();
        mCurrentWeatherAdapter.notifyDataSetChanged();
        jsonParse();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mCurrentList.clear();
        mCurrentWeatherAdapter.notifyDataSetChanged();
        jsonParse();
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
