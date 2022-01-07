package com.android.example.anytimeweather.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.anytimeweather.R;
import com.android.example.anytimeweather.ReminderBroadcast;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class SettingsActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private final String[] Temps = { "Celsius", "Fahrenheit" };
    private final String[] Time = {"Every 1 hours", "Every 3 hours", "Every 6 hours", "Every 12 hours"};

    SharedPreferences sharedPreferences1, sharedPreferences2, sharedPreferences3;
    private Spinner mSpinner1, mSpinner2;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        sharedPreferences1 = getSharedPreferences("prefAlarm", MODE_PRIVATE);
        sharedPreferences2 = getSharedPreferences("prefTempUnits", MODE_PRIVATE);
        sharedPreferences3 = getSharedPreferences("prefNotifyTime", MODE_PRIVATE);

        Switch switcher = findViewById(R.id.switcher1);

        boolean is = sharedPreferences1.getBoolean("alarm", false);
        if(is){
            switcher.setChecked(true);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(SettingsActivity.this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, 0);

                if(!isChecked){
                    alarmManager.cancel(pendingIntent);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putBoolean("alarm", false);
                    editor.apply();

                    switcher.setChecked(false);
                } else {

                    String timeNotification = sharedPreferences3.getString("notify", "Every 3 hours");

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
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putBoolean("alarm", true);
                    editor.apply();

                    switcher.setChecked(true);
                }
            }
        });

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(" Any Time Weather Settings");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//Empty
                    isShow = false;
                }
            }
        });

        ImageView imageView = findViewById(R.id.settings_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSpinner1 = findViewById(R.id.spinner1);
        String spinVal = sharedPreferences2.getString("unit", "Celsius");

        mSpinner1.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.setting_spinner_view, Temps);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner1.setAdapter(adapter);

        for(int i = 0; i<Temps.length; i++){
            if(spinVal != null && spinVal.equals(mSpinner1.getItemAtPosition(i).toString())){
                mSpinner1.setSelection(i);
                break;
            }
        }

        mSpinner2 = findViewById(R.id.spinner2);
        String spinVal2 = sharedPreferences3.getString("notify", "Every 3 hours");

        mSpinner2.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.setting_spinner_view, Time);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner2.setAdapter(adapter2);

        for(int i = 0; i<Time.length; i++){
            if(spinVal2 != null && spinVal2.equals(mSpinner2.getItemAtPosition(i).toString())){
                mSpinner2.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String mSpnVal1 = mSpinner1.getSelectedItem().toString();
        SharedPreferences.Editor editor1 = sharedPreferences2.edit();
        editor1.putString("unit", mSpnVal1);
        editor1.apply();

        String mSpnVal2 = mSpinner2.getSelectedItem().toString();
        SharedPreferences.Editor editor2 = sharedPreferences3.edit();
        editor2.putString("notify", mSpnVal2);
        editor2.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}