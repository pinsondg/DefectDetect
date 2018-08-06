package com.example.dpgra.defectdetect;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.PotholeList;

/**
 * The main activity of the android app.
 *
 * @author Daniel Pinson, Vamsi Yadav
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;
    private MenuItem currentMenuItem;


    /**
     * Determines what to do when an item on the bottom navigation bar is selected.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if ( currentFragment instanceof PotholeListFragment ) {
                ((PotholeListFragment) currentFragment).getEditText().clearFocus();
            }
            //set up fragment manager and transaction manager
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
            Fragment fragment = null;

            //switch fragments
            if ( currentMenuItem.getItemId() != item.getItemId() ) {
                transaction.remove(currentFragment);
                switch (item.getItemId()) {
                    case R.id.camera:
                        System.out.println("Selected camera");
                        fragment = CameraFragment.getInstance();
                        transaction.add(R.id.frameholder, fragment);
                        currentFragment = fragment;
                        //transaction.addToBackStack(null);
                        transaction.commitNow();
                        currentMenuItem = item;
                        return true;
                    case R.id.map:
                        System.out.println("Selected map");
                        fragment = MapFragment.getInstance();
                        transaction.add(R.id.frameholder, fragment);
                        currentFragment = fragment;
                        //transaction.addToBackStack(null);
                        transaction.commitNow();
                        currentMenuItem = item;
                        return true;
                    case R.id.list:
                        System.out.println("Selected list");
                        fragment = new PotholeListFragment();
                        transaction.add(R.id.frameholder, fragment);
                        currentFragment = fragment;
                        //transaction.addToBackStack(null);
                        currentMenuItem = item;
                        transaction.commitNow();
                        return true;
                }
            }

            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_main);
        readInData();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.map);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        currentMenuItem = bottomNavigationView.getMenu().findItem(R.id.map);
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 1);


        }
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS ) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this), 1);
        }

        //Waits for permission lol
        while( ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
        }

        //Set default fragment
        FragmentManager manager = getSupportFragmentManager();
        currentFragment = MapFragment.getInstance();
        manager.beginTransaction().add(R.id.frameholder, currentFragment).commitNow();
        //setToMapView();
    }


    @Override
    protected void onPause() {
        File fileDir = getFilesDir();
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(fileDir.getAbsolutePath() + "/pothole_list.ser");
            System.out.println("Saving hash map to " + fileDir.getAbsolutePath() + "/pothole_list.ser");
        } catch (FileNotFoundException e) {
            System.out.print("Could not create and save file.");
            return;
        }
        ObjectOutputStream outputStream = null;
        try {

            outputStream = new ObjectOutputStream(stream);
            PotholeList list = PotholeList.getInstance();
            outputStream.writeObject(list);
        } catch (IOException e) {
            System.out.println("Could not save file.");
            e.printStackTrace();
        }
        try {
            outputStream.close();
            stream.close();
        } catch (IOException e ) {
            e.printStackTrace();
        }

        super.onPause();
    }

    /**
     * Reads in the data for the app.
     *
     * @return true if data could be read in, false if otherwise
     */
    private boolean readInData() {
        File fileDir = getFilesDir();
        File readFile = null;
        for ( File file : fileDir.listFiles() ) {
            if ( file.getName().equals("pothole_list.ser")) {
                readFile = file;
                break;
            }
        }
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(readFile);
        } catch (FileNotFoundException | NullPointerException e) {
            System.out.print("File not found!");
            return false;
        }
        ObjectInputStream obInStream = null;
        try {
            obInStream = new ObjectInputStream(inStream);
            PotholeList list = (PotholeList) obInStream.readObject();
            PotholeList.getInstance().overwrite(list);
            return true;
        } catch ( IOException | ClassNotFoundException e ) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the current location of the phone.
     *
     * @return the current location
     */
    @Nullable
    public Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = null;
        LocationListener locationListener = new LocationListener() {
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
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION};

                ActivityCompat.requestPermissions(this, permissions, 0);
            }
        }
        return currentLocation;
    }

    /**
     * Sets the view to the map view.
     */
    public void setToMapView() {
        bottomNavigationView.setSelectedItemId(R.id.map);
    }

    @Override
    public void onLowMemory() {
        Toast.makeText(this,"Memory Low! Switiching back to map.", Toast.LENGTH_SHORT);
        setToMapView();
        super.onLowMemory();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("Touch Down X:" + event.getX() + " Y:" + event.getY());
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            System.out.println("Touch Up X:" + event.getX() + " Y:" + event.getY());
        }
        return super.onTouchEvent(event);
    }

}
