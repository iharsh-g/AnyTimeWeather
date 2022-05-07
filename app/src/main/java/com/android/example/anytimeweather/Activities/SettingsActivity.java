package com.android.example.anytimeweather.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.android.example.anytimeweather.LocationUtils;
import com.android.example.anytimeweather.R;
import com.android.example.anytimeweather.ReminderBroadcast;
import com.android.example.anytimeweather.databinding.ActivitySettingsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private final String[] Temps = {"Celsius", "Fahrenheit"};
    private final String[] Time = {"Every 1 hours", "Every 3 hours", "Every 6 hours", "Every 12 hours"};

    SharedPreferences sharedPreferences1, sharedPreferences2, sharedPreferences3;

    private Boolean is = false;
    private ActivitySettingsBinding mBinding;

    private FusedLocationProviderClient client;

    @SuppressLint({"UseSwitchCompatOrMaterialCode", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        sharedPreferences1 = getSharedPreferences("prefAlarm", MODE_PRIVATE);
        sharedPreferences2 = getSharedPreferences("prefTempUnits", MODE_PRIVATE);
        sharedPreferences3 = getSharedPreferences("prefNotifyTime", MODE_PRIVATE);

        is = sharedPreferences1.getBoolean("alarm", false);
        if (is) {
            mBinding.notificationSwitcher.setChecked(true);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mBinding.notificationSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(SettingsActivity.this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, 0);

                if (!isChecked) {
                    alarmManager.cancel(pendingIntent);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putBoolean("alarm", false);
                    editor.apply();

                    mBinding.notificationSwitcher.setChecked(false);
                } else {

                    String timeNotification = sharedPreferences3.getString("notify", "Every 3 hours");

                    long triggerServiceMillis, inIntervalMillis;
                    if (timeNotification.equals("Every 1 hours")) {

                        triggerServiceMillis = 1000 * 60 * 60;
                        inIntervalMillis = 1000 * 60 * 60;
                    } else if (timeNotification.equals("Every 3 hours")) {

                        triggerServiceMillis = 1000 * 60 * 60 * 3;
                        inIntervalMillis = 1000 * 60 * 60 * 3;
                    } else if (timeNotification.equals("Every 6 hours")) {

                        triggerServiceMillis = 1000 * 60 * 60 * 6;
                        inIntervalMillis = 1000 * 60 * 60 * 6;
                    } else {
                        triggerServiceMillis = 1000 * 60 * 60 * 12;
                        inIntervalMillis = 1000 * 60 * 60 * 12;
                    }

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerServiceMillis, inIntervalMillis, pendingIntent);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putBoolean("alarm", true);
                    editor.apply();

                    mBinding.notificationSwitcher.setChecked(true);
                    is = true;
                }
            }
        });

        String spinVal = sharedPreferences2.getString("unit", "Celsius");
        mBinding.spinner1.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.setting_spinner_view, Temps);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinner1.setAdapter(adapter);

        for (int i = 0; i < Temps.length; i++) {
            if (spinVal != null && spinVal.equals(mBinding.spinner1.getItemAtPosition(i).toString())) {
                mBinding.spinner1.setSelection(i);
                break;
            }
        }

        if (is) {
            mBinding.spinner2.setEnabled(true);
        } else {
            mBinding.spinner2.setEnabled(false);
        }
        String spinVal2 = sharedPreferences3.getString("notify", "Every 3 hours");

        mBinding.spinner2.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.setting_spinner_view, Time);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinner2.setAdapter(adapter2);

        for (int i = 0; i < Time.length; i++) {
            if (spinVal2 != null && spinVal2.equals(mBinding.spinner2.getItemAtPosition(i).toString())) {
                mBinding.spinner2.setSelection(i);
                break;
            }
        }


        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        if(sharedPreferences.getString("gpsCapitalName", "Give permission for GPS").equals("Give permission for GPS") || sharedPreferences.getString("gpsCountryName", "Give permission for GPS").equals("Give permission for GPS")) {
            mBinding.settingsCurrLocation.setText("Provide Location");
        }
        else {
            mBinding.settingsCurrLocation.setText(sharedPreferences.getString("gpsCapitalName", "Give permission for GPS")
                    + ", " + sharedPreferences.getString("gpsCountryName", "Give permission for GPS"));
        }

        client = LocationServices.getFusedLocationProviderClient(this);


        SharedPreferences sharedPreferencesPrefDatabase = getSharedPreferences("prefDatabase", MODE_PRIVATE);

        mBinding.switcherLocation.setChecked(sharedPreferencesPrefDatabase.getString("capitalName", "").equals(sharedPreferences.getString("gpsCapitalName", "Give permission for GPS")));
        mBinding.switcherLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mBinding.switcherLocation.setChecked(true);

                    if(mBinding.settingsCurrLocation.getText().toString().equals("Provide Location")) {
                        getCurrentLocation();
                    }
                    else {
                        String[] str = mBinding.settingsCurrLocation.getText().toString().split(", ");
                        SharedPreferences.Editor editor = sharedPreferencesPrefDatabase.edit();
                        editor.putString("capitalName", str[0]);
                        editor.putString("countryName", str[1]);
                        editor.apply();
                    }
                }
            }
        });

        mBinding.settingsRlGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        getSupportActionBar().setTitle("Any Time Weather Settings");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String mSpnVal1 = mBinding.spinner1.getSelectedItem().toString();
        SharedPreferences.Editor editor1 = sharedPreferences2.edit();
        editor1.putString("unit", mSpnVal1);
        editor1.apply();

        String mSpnVal2 = mBinding.spinner2.getSelectedItem().toString();
        SharedPreferences.Editor editor2 = sharedPreferences3.edit();
        editor2.putString("notify", mSpnVal2);
        editor2.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                            Toast.makeText(SettingsActivity.this, "getting location unsuccessful", Toast.LENGTH_SHORT).show();
                            Toast.makeText(SettingsActivity.this, "Please try to find your location in Search Location", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SettingsActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor1 = sharedPreferences.edit();
                            editor1.putString("gpsLat", Double.toString(location.getLatitude()));
                            editor1.putString("gpsLon", Double.toString(location.getLongitude()));
                            editor1.putInt("locId", 0);
                            editor1.apply();

                            Geocoder geocoder = new Geocoder(SettingsActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1);

                                SharedPreferences.Editor editor2 = sharedPreferences.edit();
                                editor2.putString("gpsCapitalName", addresses.get(0).getAdminArea());
                                editor2.putString("gpsCountryName", addresses.get(0).getCountryName());
                                editor2.apply();

                                mBinding.settingsCurrLocation.setText(addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } else {
                //settings open here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this,"Granted", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getCurrentLocation();
    }
}