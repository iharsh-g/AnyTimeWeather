package com.android.example.anytimeweather.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.anytimeweather.Activities.MainActivity;
import com.android.example.anytimeweather.Database.FavoriteData;
import com.android.example.anytimeweather.Database.RoomDB;
import com.android.example.anytimeweather.Fragments.FavoriteFragment;
import com.android.example.anytimeweather.Models.MainViewModel;
import com.android.example.anytimeweather.R;

import java.util.ArrayList;

public class FavoriteDatabaseAdapter extends RecyclerView.Adapter<FavoriteDatabaseAdapter.FavoriteDatabaseViewHolder> {

    private ArrayList<FavoriteData> dataList;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private Context mContext;

    private ArrayList<FavoriteData> mSelectedList = new ArrayList<>();
    private MainViewModel mainViewModel;
    private boolean isEnable = false, isSelectAll = false;
    private RoomDB database;

    public static ActionMode mActionMode;
    private int lastPos = -1;

    public FavoriteDatabaseAdapter(Activity activity, Context context, ArrayList<FavoriteData> data) {
        this.activity = activity;
        this.dataList = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public FavoriteDatabaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_database, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(MainViewModel.class);
        return new FavoriteDatabaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteDatabaseViewHolder holder, int position) {
        sharedPreferences = activity.getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);
        FavoriteData favoriteData = dataList.get(position);

        holder.mCapital.setText(favoriteData.getCapital());
        holder.mCountry.setText(favoriteData.getCountry());

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnable) {
                    clickItem(holder);
                } else {
                    SharedPreferences sharedPreferencesDatabase = mContext.getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferencesDatabase.edit();
                    editor.putString("capitalName", favoriteData.getCapital());
                    editor.putString("countryName", favoriteData.getCountry());
                    editor.putInt("locId", favoriteData.getLocationId());
                    editor.apply();

                    Toast.makeText(mContext, "Weather set for " + favoriteData.getCapital(), Toast.LENGTH_SHORT).show();

                    FavoriteFragment fragment = new FavoriteFragment();
                    ((MainActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, fragment, "Restart Fragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        holder.mRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

                if (!isEnable) {
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            MenuInflater menuInflater = mode.getMenuInflater();
                            menuInflater.inflate(R.menu.action_bar_favorite_fragment, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            mSelectedList.clear();
                            isEnable = true;
                            mActionMode = mode;
                            clickItem(holder);

                            mainViewModel.getString().observe((LifecycleOwner) mContext, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    if(s.equals("0")){
                                        mode.setTitle("Select Items");
                                        menu.findItem(R.id.menu_delete).setVisible(false);
                                    } else if(s.equals("1")){
                                        mode.setTitle(String.format("%s Item Selected", s));
                                        menu.findItem(R.id.menu_delete).setVisible(true);
                                    } else {
                                        mode.setTitle(String.format("%s Items Selected", s));
                                        menu.findItem(R.id.menu_delete).setVisible(true);
                                    }
                                }
                            });
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            int id = item.getItemId();

                            switch (id) {
                                case R.id.menu_delete:

                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                                    if(mode.getTitle().equals("1 Item Selected")) {
                                        builder.setTitle("Do you want to delete?");
                                    } else {
                                        builder.setTitle("Do you want to delete these items?");
                                    }

                                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            for (FavoriteData data : mSelectedList) {
                                                SharedPreferences sharedPreferences1 = mContext.getSharedPreferences("prefDatabase", Context.MODE_PRIVATE);
                                                database = RoomDB.getDatabase(mContext);

                                                if (sharedPreferences1.getInt("locId", -1) == data.getLocationId()) {
                                                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                                                    editor.putString("capitalName", "");
                                                    editor.putString("countryName", "");
                                                    editor.putInt("locId", -1);
                                                    editor.apply();
                                                }

                                                database.favoriteDao().delete(data.getLocationId());
                                                dataList.remove(data);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });

                                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.show();

                                    if (dataList.size() == 0) {
                                        holder.mRelativeLayout.setVisibility(View.VISIBLE);
                                    }
                                    mode.finish();
                                    break;

                                case R.id.menu_select_all:

                                    if (mSelectedList.size() == dataList.size()) {
                                        isSelectAll = false;
                                        mSelectedList.clear();
                                    } else {
                                        isSelectAll = true;
                                        mSelectedList.clear();
                                        mSelectedList.addAll(dataList);
                                    }

                                    mainViewModel.setString(String.valueOf(mSelectedList.size()));
                                    notifyDataSetChanged();
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            isEnable = false;
                            isSelectAll = false;
                            mActionMode = null;
                            notifyDataSetChanged();
                        }
                    };

                    mActionMode = activity.startActionMode(callback);
                } else {
                    clickItem(holder);
                }

                return true;
            }
        });

        if (sharedPreferences.getInt("locId", -1) == favoriteData.getLocationId()) {
            holder.mRelativeLayout.setBackgroundColor(Color.RED);
        } else {
            holder.mRelativeLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.gradient_5));
        }

        if (holder.getAdapterPosition() > lastPos) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.animation_1);
            holder.itemView.setAnimation(animation);
            lastPos = holder.getAdapterPosition();
        }

        if (isSelectAll) {
            holder.mIv.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.mIv.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void clickItem(FavoriteDatabaseAdapter.FavoriteDatabaseViewHolder holder) {
        FavoriteData data = dataList.get(holder.getAdapterPosition());

        if (holder.mIv.getVisibility() == View.GONE) {
            holder.mIv.setVisibility(View.VISIBLE);
            holder.mRelativeLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.gradient_2));
            mSelectedList.add(data);
        } else {
            holder.mIv.setVisibility(View.GONE);
            holder.mRelativeLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.gradient_5));
            mSelectedList.remove(data);
        }
        mainViewModel.setString(String.valueOf(mSelectedList.size()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class FavoriteDatabaseViewHolder extends RecyclerView.ViewHolder {

        private TextView mCapital;
        private TextView mCountry;
        private RelativeLayout mRelativeLayout;
        private ImageView mIv;

        public FavoriteDatabaseViewHolder(@NonNull View itemView) {
            super(itemView);

            mCapital = itemView.findViewById(R.id.text_view_cap);
            mCountry = itemView.findViewById(R.id.text_view_country);
            mRelativeLayout = itemView.findViewById(R.id.rl_database);
            mIv = itemView.findViewById(R.id.iv_check);
        }
    }
}
