package com.android.example.anytimeweather.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.example.anytimeweather.Fragments.CurrentWeatherFragment;
import com.android.example.anytimeweather.Fragments.ForecastWeatherFragment;

public class CategoryAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private final int mTabCount;

    public CategoryAdapter(Context context, @NonNull FragmentManager fm, int num) {
        super(fm);
        mContext = context;
        mTabCount = num;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new CurrentWeatherFragment();
        }
        else {
            return new ForecastWeatherFragment();
        }
    }

    @Override
    public int getCount() {
        return mTabCount;
    }
}
