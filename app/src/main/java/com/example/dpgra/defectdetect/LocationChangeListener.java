package com.example.dpgra.defectdetect;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.GoogleMap;

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
