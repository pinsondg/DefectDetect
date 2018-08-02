package com.example.dpgra.defectdetect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

public class LocationChangeListener implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnCameraMoveStartedListener {

    private MapFragment fragment;

    public LocationChangeListener(MapFragment fragment) {
        this.fragment =fragment;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onMyLocationButtonClick() {
        fragment.ButtonClicked = true;
        return fragment.ButtonClicked;
    }
    

    @Override
    public void onCameraMoveStarted(int i) {
        if ( i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE ) {
            fragment.ButtonClicked = false;
        }
    }
}
