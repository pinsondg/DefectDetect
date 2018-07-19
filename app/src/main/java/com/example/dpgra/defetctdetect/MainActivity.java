package com.example.dpgra.defetctdetect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import model.Darknet;
import model.Pothole;

public class MainActivity extends AppCompatActivity {

    private Darknet net;
    private BottomNavigationView bottomNavigationView;
    private List<Fragment> fragList;
    private Fragment currentFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = null;
            transaction.remove(currentFragment);
            switch (item.getItemId()) {
                case R.id.camera:
                    System.out.println("Selected camera");
                    fragment = CameraFragment.getInstance();
                    transaction.add(R.id.frameholder, fragment);
                    currentFragment = fragment;
                    //transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                case R.id.map:
                    System.out.println("Selected map");
                    fragment = MapFragment.getInstance();
                    transaction.add(R.id.frameholder, fragment);
                    currentFragment = fragment;
                    //transaction.addToBackStack(null);
                    //transaction.setTransition(1);
                    transaction.commit();
                    return true;
                case R.id.list:
                    System.out.println("Selected list");
                    fragment = new PotholeListFragment();
                    transaction.add(R.id.frameholder, fragment);
                    currentFragment = fragment;
                    //transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.map);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            String[] permissions = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS ) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this), 1);
        }
        //Set default fragment
        FragmentManager manager = getSupportFragmentManager();
        currentFragment = MapFragment.getInstance();
        manager.beginTransaction().add(R.id.frameholder, currentFragment).commitNow();
    }


    @Override
    protected void onDestroy() {
        File fileDir = getFilesDir();
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(fileDir.getAbsolutePath() + "/hashmap.ser");
            System.out.println("Saving hash map to " + fileDir.getAbsolutePath() + "/hashmap.ser");
        } catch (FileNotFoundException e) {
            System.out.print("Could not create and save file.");
            return;
        }
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(stream);
            HashMap<MarkerOptions, Pothole> map = (HashMap<MarkerOptions, Pothole>)CameraFragment.getInstance().getMarkerOptionsMap();
            outputStream.writeObject(map);
        } catch (IOException e) {
            System.out.println("Could not save file.");

        }
        try {
            outputStream.close();
            stream.close();
        } catch (IOException e ) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}
