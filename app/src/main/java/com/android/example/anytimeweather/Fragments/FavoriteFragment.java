package com.android.example.anytimeweather.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.example.anytimeweather.Activities.MainActivity;
import com.android.example.anytimeweather.Adapters.FavoriteDatabaseAdapter;
import com.android.example.anytimeweather.Database.FavoriteData;
import com.android.example.anytimeweather.Database.RoomDB;
import com.android.example.anytimeweather.R;
import com.android.example.anytimeweather.databinding.FragmentFavoriteBinding;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {

    public FavoriteFragment(){    }

    private ArrayList<FavoriteData> dataList = new ArrayList<>();
    private RoomDB database;
    private FavoriteDatabaseAdapter mAdapter;

    private FragmentFavoriteBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);
        View view = mBinding.getRoot();

        mBinding.favoriteRv.setVisibility(View.VISIBLE);
        mBinding.favoriteEmpty.setVisibility(View.GONE);

        database = RoomDB.getDatabase(getContext());
        dataList = (ArrayList<FavoriteData>) database.favoriteDao().getAll();

        if(dataList.isEmpty()){
            mBinding.favoriteRv.setVisibility(View.GONE);
            mBinding.favoriteEmpty.setVisibility(View.VISIBLE);
        }

        mAdapter = new FavoriteDatabaseAdapter(getActivity(), getContext(), dataList);
        mBinding.favoriteRv.setAdapter(mAdapter);
        mBinding.favoriteRv.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dataList.clear();
        dataList.addAll(database.favoriteDao().getAll());
        mAdapter.notifyDataSetChanged();
    }
}
