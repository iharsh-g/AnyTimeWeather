package com.android.example.anytimeweather;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng name = new LatLng(getIntent().getDoubleExtra("lat", 0), getIntent().getDoubleExtra("long", 0));
        String actualName = getIntent().getStringExtra("name");

        if (actualName.contains(",")) {
            String[] part = actualName.split(",");
            actualName = part[0] + ", " + part[2];
        }

        googleMap.addMarker(new MarkerOptions().position(name).title(actualName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(name));
    }
}