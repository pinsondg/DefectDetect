package com.example.dpgra.defetctdetect;

import android.Manifest;
import android.content.pm.PackageManager;
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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import model.Darknet;
import model.PotholeList;

/**
 * The main activity of the android app.
 */
public class MainActivity extends AppCompatActivity {

    private Darknet net;
    private BottomNavigationView bottomNavigationView;
    private List<Fragment> fragList;
    private Fragment currentFragment;
    private MenuItem currentMenuItem;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = null;
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
        setContentView(R.layout.activity_main);
        readInData();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.map);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        currentMenuItem = bottomNavigationView.getMenu().findItem(R.id.map);
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
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
            //writer.write("");
            //writer.close();
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

    public void setToMapView() {
        bottomNavigationView.setSelectedItemId(R.id.map);
    }
}
