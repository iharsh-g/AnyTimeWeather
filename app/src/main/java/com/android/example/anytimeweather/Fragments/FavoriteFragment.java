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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.example.anytimeweather.Activities.MainActivity;
import com.android.example.anytimeweather.Adapters.FavoriteDatabaseAdapter;
import com.android.example.anytimeweather.Database.FavoriteData;
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

public class FavoriteFragment extends Fragment implements FavoriteDatabaseAdapter.ItemClicked {

    public FavoriteFragment(){

    }

    private FusedLocationProviderClient client;

    private ArrayList<FavoriteData> dataList = new ArrayList<>();
    private RoomDB database;
    private FavoriteDatabaseAdapter mAdapter;
    private SharedPreferences sharedPreferences;

    private int position;
    private ActionMode mActionMode;

    private FavoriteData data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferencesDatabase = requireContext().getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ListView listView = view.findViewById(R.id.lv_favorite);
        TextView mCurrentCapital = view.findViewById(R.id.curr_location_cap);
        TextView mCurrentCountry = view.findViewById(R.id.curr_location_country);
        RelativeLayout relativeLayout = view.findViewById(R.id.empty_rl);
        RelativeLayout mCurrentLocationLayout = view.findViewById(R.id.curr_location_rl);

        listView.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);

        database = RoomDB.getDatabase(getContext());
        dataList = (ArrayList<FavoriteData>) database.favoriteDao().getAll();

        if(dataList.isEmpty()){
            listView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }

        mAdapter = new FavoriteDatabaseAdapter(getActivity(), dataList, this);
        listView.setAdapter(mAdapter);

        mCurrentCapital.setText(sharedPreferences.getString("gpsCapitalName", "Give permission for GPS"));
        mCurrentCountry.setText(sharedPreferences.getString("gpsCountryName", "Give permission for GPS"));

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        mCurrentLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                getAccess();
            }
        });

        if(sharedPreferencesDatabase.getInt("locId", 0) == 0){
            mCurrentLocationLayout.setBackgroundColor(Color.RED);
        } else {
            mCurrentLocationLayout.setBackgroundColor(Color.parseColor("#303030"));
        }

        mCurrentLocationLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
                return true;
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

    @Override
    public void onItemClicked(int pos) {
        if(mActionMode != null){
            mActionMode = null;
        }
        position = pos;
        mActionMode = getActivity().startActionMode(mActionModeCallback);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_bar_favorite_fragment, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if(item.getItemId() == R.id.delete){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("Do you want to delete?");
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);
                        database = RoomDB.getDatabase(getContext());
                        FavoriteData favoriteData = dataList.get(position);

                        if(sharedPreferences1.getInt("locId", -1) == favoriteData.getLocationId()){
                            SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.putString("capitalName", "");
                            editor.putString("countryName", "");
                            editor.putInt("locId", -1);
                            editor.apply();
                        }

                        database.favoriteDao().delete(favoriteData.getLocationId());
                        dataList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    public void onPause() {
        super.onPause();

        if(mActionMode != null){
            mActionMode.finish();
            mActionMode = null;
        }
    }
}
