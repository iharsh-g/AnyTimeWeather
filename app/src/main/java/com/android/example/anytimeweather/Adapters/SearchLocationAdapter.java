package com.android.example.anytimeweather.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.anytimeweather.Models.SearchLocation;
import com.android.example.anytimeweather.R;
import com.android.example.anytimeweather.Activities.SearchedWeatherActivity;

import java.util.ArrayList;

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.SearchLocationViewHolder> {

    private Context mContext;
    private ArrayList<SearchLocation> mSearchLocationArrayList;

    final private MapImageViewClickListener mMapImageViewClickListener;

    private int lastPos = -1;

    public interface MapImageViewClickListener {
        void onClick(int clickedItemIndex, double lat, double lon, String name);
    }

    public SearchLocationAdapter(Context context, ArrayList<SearchLocation> arrayList, MapImageViewClickListener clickListener) {
        this.mContext = context;
        this.mSearchLocationArrayList = arrayList;
        this.mMapImageViewClickListener = clickListener;
    }

    @NonNull
    @Override
    public SearchLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_search, parent, false);
        return new SearchLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchLocationViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SearchLocation currentItem = mSearchLocationArrayList.get(position);
        String name = currentItem.getSearchName();
        if (name.contains(",")) {
            String[] part = name.split(",");
            name = part[0];
        }
        holder.mName.setText(name);

        holder.mCapital.setText(currentItem.getSearchCapital());

        holder.mCountry.setText(currentItem.getSearchCountry());

        if(position > lastPos){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.animation_1);
            holder.itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return mSearchLocationArrayList.size();
    }

    class SearchLocationViewHolder extends RecyclerView.ViewHolder {

        private TextView mName, mCapital, mCountry;
        private ImageView mMaps;
        private RelativeLayout mLayout;

        public SearchLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.location_name);
            mCapital = itemView.findViewById(R.id.location_capital);
            mCountry = itemView.findViewById(R.id.location_country);
            mMaps = itemView.findViewById(R.id.location_open_map);
            mLayout = itemView.findViewById(R.id.search_rl_all_text);

            mMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clicked = getAdapterPosition();
                    mMapImageViewClickListener.onClick(
                            clicked,
                            mSearchLocationArrayList.get(clicked).getSearchLat(),
                            mSearchLocationArrayList.get(clicked).getSearchLon(),
                            mSearchLocationArrayList.get(clicked).getSearchName());
                }
            });

            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clicked = getAdapterPosition();
                    Intent intent = new Intent(mContext, SearchedWeatherActivity.class);
                    intent.putExtra("name", mSearchLocationArrayList.get(clicked).getSearchName());
                    intent.putExtra("locId", mSearchLocationArrayList.get(clicked).getLocId());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
