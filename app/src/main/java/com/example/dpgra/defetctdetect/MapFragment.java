package com.example.dpgra.defetctdetect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private MapView mapView;
    private GoogleMap gmap;

    private static MapFragment mapFragment;

    @SuppressLint("ValidFragment")
    private MapFragment() {
        super();
    }

    public static MapFragment getInstance() {
        if ( mapFragment == null ) {
            mapFragment = new MapFragment();
        }
        return mapFragment;
    }

    public GoogleMap getGmap() {
        return gmap;
    }

    public MapView getMapView() {
        return mapView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.mapfragment, container, false);
        MapsInitializer.initialize(this.getActivity());

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getActivity());
        System.out.println("ON MAP READY!");
        gmap = googleMap;

        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            ActivityCompat.requestPermissions(this.getActivity(), permissions, 1);
        }
        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        gmap.setMyLocationEnabled(true);

        Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if ( loc == null ) {
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else if ( loc != null ) {
            LatLng myLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, (float) 14.25));
        } else {
            System.err.print("Could not get location.");
        }

    }

    public void setGmap( GoogleMap gmap ) {
        if ( gmap != null ) {
            this.gmap = gmap;
        }
    }

    @Override
    public void onStart() {
        mapView.onStart();
        super.onStart();
    }

    @Override
    public void onResume() {
        System.out.print("MAP RUSUMED!");
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        System.out.println("MAP PAUSED!");
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
