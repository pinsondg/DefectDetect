package com.example.dpgra.defetctdetect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import model.Pothole;
import model.PotholeList;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private MapView mapView;
    private GoogleMap gmap;
    private LatLng customLocation;
    private HashMap<MarkerOptions, Pothole> pothole_map;
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
        try {
            MapsInitializer.initialize(this.getActivity());
            System.out.println("ON MAP READY!");
            gmap = googleMap;
            gmap.setOnMarkerClickListener(new MapClickListener(getActivity()));
            PotholeList potholeList = CameraFragment.getInstance().getPotholeList();
            if (  potholeList != null && !potholeList.isEmpty() ) {
                Iterator<Pothole> i = potholeList.iterator();
                while ( i.hasNext() ) {
                    Pothole temp_pothole;
                    //Assigns pothole and then increments iterator
                    temp_pothole = i.next();
                    MarkerOptions option = new MarkerOptions().position(new LatLng(temp_pothole.getLat(),temp_pothole.getLon()));
                    Marker marker = gmap.addMarker(option);
                    marker.setTag(temp_pothole);
                }
            }
            ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
            gmap.setMyLocationEnabled(true);
            Location loc = null;
            if ( manager.getNetworkInfo(0).getDetailedState() == NetworkInfo.DetailedState.CONNECTED ) {
                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if ( loc != null && customLocation == null) {
                moveToLocation(loc.getLatitude(), loc.getLongitude());
            } else {
                moveToLocation( customLocation.latitude, customLocation.longitude );
            }

        } catch(SecurityException e) {
            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this.getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION};

                ActivityCompat.requestPermissions(this.getActivity(), permissions, 0);
            }
        }
        customLocation = null;

    }

    public void setCustomLocation( LatLng latlng ) {
        customLocation = latlng;
    }

    public void moveToLocation( double lat, double lng ) {
        LatLng location = new LatLng(lat, lng);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, (float) 14.25));
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
