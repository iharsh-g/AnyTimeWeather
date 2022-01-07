package com.android.example.anytimeweather;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.example.anytimeweather.Activities.MainActivity;
import com.android.example.anytimeweather.Adapters.CurrentWeatherAdapter;
import com.android.example.anytimeweather.Fragments.CurrentWeatherFragment;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    @SuppressLint("CheckResult")
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefWid", Context.MODE_PRIVATE);

        // Construct the RemoteViews object
        @SuppressLint("RemoteViewLayout")
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        Log.e("WeatherWidget", "" + sharedPreferences.getString("cap", "sda"));
        Log.e("WeatherWidget", "" + sharedPreferences.getString("cou", "@3"));
        Log.e("WeatherWidget", "" + sharedPreferences.getString("date", "23") + " | " + sharedPreferences.getString("time", "23"));
        Log.e("WeatherWidget", "" + sharedPreferences.getString("temp", "2"));
        Log.e("WeatherWidget", "" + sharedPreferences.getString("feels", "2"));
        Log.e("WeatherWidget", "" + sharedPreferences.getString("uv", "3"));
        Log.e("WeatherWidget", "" + sharedPreferences.getString("icon", "2"));
        //Log.e("WeatherWidget", "" + bitmap[0]);

        views.setTextViewText(R.id.widget_date, sharedPreferences.getString("date", "23"));
        views.setTextViewText(R.id.widget_time, sharedPreferences.getString("time", "23"));
        views.setTextViewText(R.id.widget_temp1, sharedPreferences.getString("temp", "2"));
        views.setTextViewText(R.id.widget_region, sharedPreferences.getString("cap", "sda") + ", " + sharedPreferences.getString("cou", "@3"));
        views.setTextViewText(R.id.widget_temp2, sharedPreferences.getString("feels", "2"));
        views.setTextViewText(R.id.widget_uv, sharedPreferences.getString("uv", "3"));


        Intent intent1 = new Intent(context, WeatherWidget.class);
        intent1.setAction("sync");
        intent1.putExtra("widgetId", appWidgetId);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.sync, pendingIntent1);

        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.setAction("open");
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
        views.setOnClickPendingIntent(R.id.widget_temp1, pendingIntent2);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("sync".equals(intent.getAction())) {
            updateAppWidget(context, AppWidgetManager.getInstance(context), intent.getIntExtra("widgetId", 0));
        } else {
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.e("WeatherWidget", "onEnabledCalled ");
        //mContext = context;
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}