package com.example.dpgra.defectdetect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;

public class LocationChangeListener implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnCameraMoveListener{

    private Activity activity;
    private Context context;
    private MapFragment fragment;
    private LocationManager locationManager;

    public LocationChangeListener(Activity activity, Context context, MapFragment fragment) {
        this.activity = activity;
        this.context = context;
        this.fragment =fragment;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onMyLocationButtonClick() {
        fragment.ButtonClicked = true;
        return fragment.ButtonClicked;
    }

    @Override
    public void onCameraMove() {
        fragment.ButtonClicked = false;
    }
}
