package com.android.example.anytimeweather.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.anytimeweather.Adapters.SearchLocationAdapter;
import com.android.example.anytimeweather.Map;
import com.android.example.anytimeweather.Models.SearchLocation;
import com.android.example.anytimeweather.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity
        implements SearchLocationAdapter.MapImageViewClickListener {

    private ArrayList<SearchLocation> mLocationList;
    private RequestQueue mQueue;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private SearchLocationAdapter mSearchLocationAdapter;
    private AutoCompleteTextView mEditText;
    private ArrayList<String> list;
    private RelativeLayout mRlEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mLocationList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_search);
        mProgressBar = findViewById(R.id.search_pb);
        mQueue = Volley.newRequestQueue(this);
        mEditText = findViewById(R.id.search_edit_text);
        mRlEmpty = findViewById(R.id.rl_search_empty);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSearchLocationAdapter = new SearchLocationAdapter(this, mLocationList, this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mSearchLocationAdapter);

        mProgressBar.setVisibility(View.GONE);
        mRlEmpty.setVisibility(View.VISIBLE);

        loadData();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        mEditText.setAdapter(adapter);

        showKeyboard();

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    search(v.getText().toString());
                    mEditText.dismissDropDown();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

        TextInputLayout textInputLayout = findViewById(R.id.textInput);
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard();
            }
        });

        textInputLayout.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showKeyboard(){
        mEditText.setText("");
        mEditText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void search(String query){
        if(!query.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mRlEmpty.setVisibility(View.GONE);
            mLocationList.clear();
            mSearchLocationAdapter.notifyDataSetChanged();
            jsonParse(query);
        } else {
            Toast.makeText(this, "Pass Some query", Toast.LENGTH_SHORT).show();
            mRlEmpty.setVisibility(View.VISIBLE);
        }

    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("listPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("countries", null);

        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        list = gson.fromJson(json, type);

        if(list == null){
            list = new ArrayList<>();
        }
    }

    private void jsonParse(String q){
        String url = null;
        if(q != null){
            url = "https://api.weatherapi.com/v1/search.json?key=730b153a05d74989aef61637211811&q=" + q;
        }
        Log.d("SearchActivity", url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(!list.contains(q)){  //Check if that string not present in the list
                            list.add(q);        //Basically not to repeat same string in the list
                            saveData();
                        }
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);

                        for(int i = 0; i < response.length(); i++ ){

                            try {
                                JSONObject current = response.getJSONObject(i);
                                int id = current.getInt("id");
                                String name = current.getString("name");
                                String cap = current.getString("region");
                                String country = current.getString("country");
                                double lat = current.getDouble("lat");
                                double lon = current.getDouble("lon");

                                SearchLocation searchLocation = new SearchLocation(id, name, country, cap, lat, lon);
                                mLocationList.add(searchLocation);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(SearchActivity.this, "Pass Another result", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(request);
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("listPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("countries", json);

        editor.apply();
    }

    @Override
    public void onClick(int clickedItemIndex, double lat, double lon, String name) {
        Intent intent = new Intent(this, Map.class);
        intent.putExtra("lat", lat);
        intent.putExtra("long", lon);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}
