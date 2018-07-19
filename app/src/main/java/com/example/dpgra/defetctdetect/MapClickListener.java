package com.example.dpgra.defetctdetect;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapClickListener implements GoogleMap.OnMarkerClickListener {

    public MapClickListener() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
