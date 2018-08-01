package com.example.dpgra.defectdetect;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;

import model.Pothole;
import model.PotholeList;

/**
 * The map section of the application. Shows the user where they are and where all of the potholes
 * the app detected are.
 *
 * @author Daniel Pinson, Vamsi Yadav
 * @version 1.0
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private MapView mapView;
    private GoogleMap gmap;
    private LatLng customLocation;
    private static MapFragment mapFragment;
    private LocationManager locationManager;

    public boolean ButtonClicked = true;

    @SuppressLint("ValidFragment")
    private MapFragment() {
        super();
    }

    /**
     * Singleton method make it easy to access the only one.
     * @return the instance of MapFragment
     */
    public static MapFragment getInstance() {
        if ( mapFragment == null ) {
            mapFragment = new MapFragment();
        }
        return mapFragment;
    }

    /**
     * Returns the current GoogleMap (note: the google map is changed every time this fragment is
     * swiched back to)
     * @return
     */
    public GoogleMap getGmap() {
        return gmap;
    }

    /**
     * Gets the map view.
     *
     * @return the map view
     */
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
            //System.out.println("ON MAP READY!");
            gmap = googleMap;
            gmap.setOnMarkerClickListener(new MapClickListener(getActivity()));
            PotholeList potholeList = PotholeList.getInstance();
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
            gmap.setMyLocationEnabled(true);

            Location loc = ((MainActivity)this.getActivity()).getLocation();

            //Listens for location updates and calls onLocationChange()
            gmap.setOnMyLocationButtonClickListener(new LocationChangeListener(getActivity(), getContext(), mapFragment));

            locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
            //gmap.setOnCameraMoveListener(new LocationChangeListener(getActivity(), getContext(), mapFragment));

            if ( loc != null && customLocation == null) {
                moveToLocation(loc.getLatitude(), loc.getLongitude());
            } else if(customLocation != null){
                ButtonClicked = false;
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

    /**
     * Set the location to go to next time the map is shown.
     *
     * @param latlng the latitude and longitude to show
     */
    public void setCustomLocation( LatLng latlng ) {
        customLocation = latlng;
    }

    /**
     * Moves the map to a certain location.
     *
     * @param lat the latitude to move to
     * @param lng the longitude to move to
     */
    private void moveToLocation( double lat, double lng ) {
        LatLng location = new LatLng(lat, lng);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, (float) 14.75));
    }

    @Override
    public void onStart() {
        mapView.onStart();
        super.onStart();
    }

    @Override
    public void onResume() {
        //System.out.print("MAP RUSUMED!");
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        //System.out.println("MAP PAUSED!");
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
        if(ButtonClicked) {
            Log.i("Message: ", "Location changed, " + location.getAccuracy() + " , " + location.getLatitude() + "," + location.getLongitude());
            CameraUpdate locUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), gmap.getCameraPosition().zoom);
            gmap.animateCamera(locUpdate);
        }
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
