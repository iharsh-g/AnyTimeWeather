package com.android.example.anytimeweather.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.example.anytimeweather.Adapters.FavoriteDatabaseAdapter;
import com.android.example.anytimeweather.Fragments.FavoriteFragment;
import com.android.example.anytimeweather.Fragments.HomeFragment;
import com.android.example.anytimeweather.Fragments.SearchFragment;
import com.android.example.anytimeweather.R;
import com.android.example.anytimeweather.ReminderBroadcast;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFragment()).commit();

        MeowBottomNavigation meowBottomNavigation = findViewById(R.id.bottom_nav);
        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.menu_search));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_without_border_favorite));

        meowBottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment currentFragment = null;
                switch (item.getId()){
                    case 1:
                        if(FavoriteDatabaseAdapter.mActionMode != null) {
                            FavoriteDatabaseAdapter.mActionMode.finish();
                        }
                        currentFragment = new HomeFragment();
                        break;
                    case 2:
                        if(FavoriteDatabaseAdapter.mActionMode != null) {
                            FavoriteDatabaseAdapter.mActionMode.finish();
                        }
                        currentFragment = new SearchFragment();
                        break;
                    case 3:
                        if(FavoriteDatabaseAdapter.mActionMode != null) {
                            FavoriteDatabaseAdapter.mActionMode.finish();
                        }
                        currentFragment = new FavoriteFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, currentFragment).commit();

            }
        });

        meowBottomNavigation.show(1, true);
        meowBottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                if(item.getId() == 1){
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                } else if(item.getId() == 2) {
                    Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        meowBottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                if(item.getId() == 1){
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                } else if(item.getId() == 2) {
                    Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        CreateNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void CreateNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("100", "Temperature Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for Temperature Notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("prefAlarm", MODE_PRIVATE);
        boolean is = sharedPreferences.getBoolean("alarm", false);

        if(is){
            Toast.makeText(getApplicationContext(), "Notification Reminder Set", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            SharedPreferences preferences = getSharedPreferences("prefNotifyTime", MODE_PRIVATE);
            String timeNotification = preferences.getString("notify", "Every 3 hours");

            long triggerServiceMillis, inIntervalMillis;
            if(timeNotification.equals("Every 1 hours")){

                triggerServiceMillis = 1000 * 60 * 60;
                inIntervalMillis = 1000 * 60 * 60;
            } else if(timeNotification.equals("Every 3 hours")){

                triggerServiceMillis = 1000 * 60 * 60 * 3;
                inIntervalMillis = 1000 * 60 * 60 * 3;
            } else if(timeNotification.equals("Every 6 hours")) {

                triggerServiceMillis = 1000 * 60 * 60 * 6;
                inIntervalMillis = 1000 * 60 * 60 * 6;
            } else {
                triggerServiceMillis = 1000 * 60 * 60 * 12;
                inIntervalMillis = 1000 * 60 * 60 * 12;
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerServiceMillis, inIntervalMillis, pendingIntent);
        }
    }
}