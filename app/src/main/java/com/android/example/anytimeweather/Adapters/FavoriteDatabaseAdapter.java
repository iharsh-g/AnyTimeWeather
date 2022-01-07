package com.android.example.anytimeweather.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ActionMode;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.example.anytimeweather.Activities.MainActivity;
import com.android.example.anytimeweather.Database.FavoriteData;
import com.android.example.anytimeweather.Fragments.FavoriteFragment;
import com.android.example.anytimeweather.R;
import com.android.example.anytimeweather.Database.RoomDB;

import java.util.ArrayList;

public class FavoriteDatabaseAdapter extends ArrayAdapter<FavoriteData> {
    private ArrayList<FavoriteData> dataList;
    private Activity activity;
    SharedPreferences sharedPreferences;

    private ItemClicked mItemClicked;

    public interface ItemClicked{
        void onItemClicked(int pos);
    }

    private int lastPos = -1;

    public FavoriteDatabaseAdapter(Activity activity, ArrayList<FavoriteData> data, ItemClicked clicked){
        super(activity, 0, data);
        this.activity = activity;
        this.dataList = data;
        this.mItemClicked = clicked;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        sharedPreferences = activity.getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(activity).inflate(R.layout.list_item_database, parent, false);
        }

        TextView capital = view.findViewById(R.id.text_view_cap);
        TextView country = view.findViewById(R.id.text_view_country);
        RelativeLayout relativeLayout = view.findViewById(R.id.rl_database);

        FavoriteData favoriteData = dataList.get(position);

        capital.setText(favoriteData.getCapital());
        country.setText(favoriteData.getCountry());

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("capitalName", favoriteData.getCapital());
                editor.putString("countryName", favoriteData.getCountry());
                editor.putInt("locId", favoriteData.getLocationId());
                editor.apply();

                Toast.makeText(activity, "Weather set for " + favoriteData.getCapital(), Toast.LENGTH_SHORT).show();

                FavoriteFragment fragment = new FavoriteFragment();
                ((MainActivity)getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_container, fragment, "Restart Fragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                mItemClicked.onItemClicked(position);
                return true;
            }
        });

        if(sharedPreferences.getInt("locId", -1) == favoriteData.getLocationId()){
            relativeLayout.setBackgroundColor(Color.RED);
            notifyDataSetChanged();
        } else {
            relativeLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.gradient_5));
        }

        if( position > lastPos ){
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.animation_1);
            view.setAnimation(animation);
            lastPos = position;
        }
        return view;
    }

}