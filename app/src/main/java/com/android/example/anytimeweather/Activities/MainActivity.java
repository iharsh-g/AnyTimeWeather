package com.android.example.anytimeweather.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.example.anytimeweather.Adapters.FavoriteDatabaseAdapter;
import com.android.example.anytimeweather.Database.FavoriteData;
import com.android.example.anytimeweather.Database.RoomDB;
import com.android.example.anytimeweather.Fragments.FavoriteFragment;
import com.android.example.anytimeweather.Fragments.HomeFragment;
import com.android.example.anytimeweather.LocationUtils;
import com.android.example.anytimeweather.R;
import com.android.example.anytimeweather.ReminderBroadcast;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private FusedLocationProviderClient client;
    public static BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ---------------------------------------- Bottom Navigation Bar -----------------------------------*/
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mBottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);

        /* ----------------------------------------- Toolbar Settings -------------------------------------------  */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imageView1 = findViewById(R.id.action_bar_app_icon_add);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        ImageView imageView2 = findViewById(R.id.action_bar_app_icon_more);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        /*  ------------- Notification -------- */
        CreateNotification();

        /* -----------------------------------------  Location Access  ------------------------------------------------*/
        SharedPreferences sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (sharedPreferences.getString("gpsLat", "").equals("")) {
            getCurrentLocation();
        }
    }

    public void CreateNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("100", "Temperature Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for Temperature Notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getCurrentLocation() {
        if(LocationUtils.checkPermission(this)) {

            if(LocationUtils.isLocationEnable(this)) {
                //Find Lat & Lon
                client.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if(location == null){
                            Toast.makeText(MainActivity.this, "There is some problem go to Settings for the location", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Please try to refresh!", Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor1 = sharedPreferences.edit();
                            editor1.putString("gpsLat", Double.toString(location.getLatitude()));
                            editor1.putString("gpsLon", Double.toString(location.getLongitude()));
                            editor1.apply();

                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1);

                                SharedPreferences.Editor editor2 = sharedPreferences.edit();
                                editor2.putString("gpsCapitalName", addresses.get(0).getAdminArea());
                                editor2.putString("gpsCountryName", addresses.get(0).getCountryName());
                                editor2.apply();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } else {
                //settings open here
                Toast.makeText(MainActivity.this, "Turn on location", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        } else {
            //request permission
            LocationUtils.requestPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,"Granted", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            } else {

                ArrayList<FavoriteData> dataList = new ArrayList<>();
                RoomDB database;

                database = RoomDB.getDatabase(this);
                dataList = (ArrayList<FavoriteData>) database.favoriteDao().getAll();

                if(dataList.size() == 0) {
                    Toast.makeText(MainActivity.this, "Try to add some locations", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                }
                else {

                    SharedPreferences sharedPreferences = getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);
                    if(sharedPreferences.getString("capitalName", "").equals("")) {
                        Toast.makeText(MainActivity.this, "Select location", Toast.LENGTH_SHORT).show();

                        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new FavoriteFragment()).commit();
                        mBottomNavigationView.setSelectedItemId(R.id.bottom_nav_fav);
                    }
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("prefAlarm", MODE_PRIVATE);
        boolean is = sharedPreferences.getBoolean("alarm", false);

        if(is){
            Intent intent = new Intent(this, ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            SharedPreferences preferences = getSharedPreferences("prefNotifyTime", MODE_PRIVATE);
            String timeNotification = preferences.getString("notify", "Every 3 hours");

            long triggerServiceMillis, inIntervalMillis;
            switch (timeNotification) {
                case "Every 1 hours":

                    triggerServiceMillis = 1000 * 60 * 60;
                    inIntervalMillis = 1000 * 60 * 60;
                    break;
                case "Every 3 hours":

                    triggerServiceMillis = 1000 * 60 * 60 * 3;
                    inIntervalMillis = 1000 * 60 * 60 * 3;
                    break;
                case "Every 6 hours":

                    triggerServiceMillis = 1000 * 60 * 60 * 6;
                    inIntervalMillis = 1000 * 60 * 60 * 6;
                    break;
                default:
                    triggerServiceMillis = 1000 * 60 * 60 * 12;
                    inIntervalMillis = 1000 * 60 * 60 * 12;
                    break;
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerServiceMillis, inIntervalMillis, pendingIntent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFragment()).commit();
                return true;

            case R.id.bottom_nav_fav:
                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new FavoriteFragment()).commit();
                return true;
        }
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("gpsLat", "").equals("")) {
            getCurrentLocation();
        }
    }
}