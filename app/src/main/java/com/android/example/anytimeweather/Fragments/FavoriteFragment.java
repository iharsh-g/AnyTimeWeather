package com.android.example.anytimeweather.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.anytimeweather.Activities.MainActivity;
import com.android.example.anytimeweather.Adapters.FavoriteDatabaseAdapter;
import com.android.example.anytimeweather.Database.FavoriteData;
import com.android.example.anytimeweather.Models.MainViewModel;
import com.android.example.anytimeweather.R;
import com.android.example.anytimeweather.Database.RoomDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoriteFragment extends Fragment {

    public FavoriteFragment(){    }

    private FusedLocationProviderClient client;

    private ArrayList<FavoriteData> dataList = new ArrayList<>();
    private RoomDB database;
    private FavoriteDatabaseAdapter mAdapter;
    private SharedPreferences sharedPreferences;

    private RelativeLayout mRl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferencesDatabase = requireContext().getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.lv_favorite);
        TextView mCurrentCapital = view.findViewById(R.id.curr_location_cap);
        TextView mCurrentCountry = view.findViewById(R.id.curr_location_country);
        mRl = view.findViewById(R.id.empty_rl);
        RelativeLayout mCurrentLocationLayout = view.findViewById(R.id.curr_location_rl);

        recyclerView.setVisibility(View.VISIBLE);
        mRl.setVisibility(View.GONE);

        database = RoomDB.getDatabase(getContext());
        dataList = (ArrayList<FavoriteData>) database.favoriteDao().getAll();

        if(dataList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            mRl.setVisibility(View.VISIBLE);
        }

        mAdapter = new FavoriteDatabaseAdapter(getActivity(), getContext(), dataList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mCurrentCapital.setText(sharedPreferences.getString("gpsCapitalName", "Give permission for GPS"));
        mCurrentCountry.setText(sharedPreferences.getString("gpsCountryName", "Give permission for GPS"));

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        mCurrentLocationLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getAccess();
                return true;
            }
        });

        if(sharedPreferencesDatabase.getInt("locId", 0) == 0){
            mCurrentLocationLayout.setBackgroundColor(Color.RED);
        } else {
            mCurrentLocationLayout.setBackgroundColor(Color.parseColor("#303030"));
        }

        mCurrentLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferencesDatabase.edit();
                editor.putString("capitalName", sharedPreferences.getString("gpsCapitalName", "Give permission for GPS"));
                editor.putString("countryName", sharedPreferences.getString("gpsCountryName", "Give permission for GPS"));
                editor.putInt("locId", 0);
                editor.apply();

                Toast.makeText(requireContext(), "Weather set for " + sharedPreferences.getString("gpsCapitalName", "Give permission for GPS"), Toast.LENGTH_SHORT).show();

                FavoriteFragment fragment = new FavoriteFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_container, fragment, "Restart Fragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        return view;
    }

    private void getAccess() {
        if (ContextCompat.checkSelfPermission(getActivity()
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&  ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        } else {
            //When permission is denied
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
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

                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("CurrentCapital", addresses.get(0).getAdminArea());
                            editor.putString("CurrentCountry", addresses.get(0).getCountryName());
                            Log.e("CurrentCapital", addresses.get(0).getAdminArea() + "");
                            Log.e("CurrentCountry", "" + addresses.get(0).getCountryName());
                            editor.apply();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("capitalName", Double.toString(location.getLatitude()));
                        editor.putString("countryName", Double.toString(location.getLongitude()));
                        editor.apply();
                        Log.e("MainActivityLocation", location.getLatitude() + "");
                        Log.e("MainActivityLocation", "" + location.getLongitude());

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

                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(
                                            location1.getLatitude(), location1.getLongitude(), 1);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("CurrentCapital", addresses.get(0).getAdminArea());
                                    editor.putString("CurrentCountry", addresses.get(0).getCountryName());
                                    Log.e("CurrentCapital", addresses.get(0).getAdminArea() + "");
                                    Log.e("CurrentCountry", "" + addresses.get(0).getCountryName());
                                    editor.apply();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("capitalName", Double.toString(location1.getLatitude()));
                                editor.putString("countryName", Double.toString(location1.getLongitude()));
                                editor.apply();
                                Log.e("MainActivityNewLocation", location1.getLatitude() + "");
                                Log.e("MainActivityNewLocation", "" + location1.getLongitude());
                            }
                        };

                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });

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

    @Override
    public void onResume() {
        super.onResume();
        dataList.clear();
        dataList.addAll(database.favoriteDao().getAll());
        mAdapter.notifyDataSetChanged();
    }
}
