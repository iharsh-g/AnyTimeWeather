package com.android.example.anytimeweather;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.example.anytimeweather.Activities.MainActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class ReminderBroadcast extends BroadcastReceiver {

    private RequestQueue mQueue;
    private Context mCon;
    private String mCapital = null, mCountry = null, mIcon = null;
    private double mTemp = 0;
    private SharedPreferences sharedPreferences, sharedPreferences2;
    private Bitmap mBitmap;

    @Override
    public void onReceive(Context context, Intent intent) {
        mQueue = Volley.newRequestQueue(context);
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        sharedPreferences2 = context.getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);

        jsonParse();

        mCon = context;
    }

    private void jsonParse() {
        String url;
        if(!sharedPreferences2.getString("capitalName", "").equals("") && !sharedPreferences2.getString("countryName", "").equals("")){
            url = "https://api.weatherapi.com/v1/current.json?key=730b153a05d74989aef61637211811&q=" + sharedPreferences2.getString("capitalName", "") + " "
                    + sharedPreferences2.getString("countryName", "");
        } else {
            url = "https://api.weatherapi.com/v1/current.json?key=730b153a05d74989aef61637211811&q=" + sharedPreferences.getString("capitalName", "") + " "
                    + sharedPreferences.getString("countryName", "");
        }

        //Log.e("ReminderBroadcast", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("location");
                            mCapital = jsonObject.getString("name");
                            mCountry = jsonObject.getString("country");

                            JSONObject currentJsonObject = response.getJSONObject("current");
                            mTemp = currentJsonObject.getDouble("temp_c");

                            JSONObject conditionJsonObject = currentJsonObject.getJSONObject("condition");
                            mIcon = "https:" + conditionJsonObject.getString("icon");

                            Picasso.get().load(mIcon).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    mBitmap = bitmap;
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                            Intent intent = new Intent(mCon, MainActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(mCon, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(mCon, "100")
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setLargeIcon(mBitmap)
                                    .setContentTitle((int) mTemp + "")
                                    .setContentText(mCapital + " | " + mCountry)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mCon);
                            notificationManagerCompat.notify(200, builder.build());

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
}
