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

import java.util.List;

import model.Darknet;

public class MainActivity extends AppCompatActivity {

    private Darknet net;
    private BottomNavigationView bottomNavigationView;
    private List<Fragment> fragList;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.camera:
                    System.out.println("Selected camera");
                    transaction.replace(R.id.frameholder, manager.findFragmentById(R.id.cameraFrag)).commit();
                    return true;
                case R.id.map:
                    System.out.println("Selected map");
                    transaction.replace(R.id.frameholder, manager.findFragmentById(R.id.mapFrag)).commit();
                    return true;
                case R.id.list:
                    System.out.println("Selected list");
                    transaction.replace(R.id.frameholder, manager.findFragmentById(R.id.listFrag)).commit();
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
        bottomNavigationView.setSelectedItemId(R.id.camera);
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
       // mTextMessage = (TextView) findViewById(R.id.message);
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_dashboard);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.CameraView);
        //mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        //mOpenCvCameraView.setCvCameraViewListener(this);
        /*
        File fileDir = getFilesDir();
        System.out.println(fileDir.getAbsolutePath());
        File cfgFile = null;
        File weightsFile = null;
        for ( File file : fileDir.listFiles() ) {
            if ( file.getName().endsWith(".cfg") ) {
                cfgFile = file;
            } else if ( file.getName().endsWith(".weights") ) {
                weightsFile = file;
            }
            System.out.println(file.getAbsolutePath());
        }
        if ( cfgFile != null && weightsFile != null ) {
            net = new Darknet(cfgFile.getAbsolutePath(), weightsFile.getAbsolutePath());
        }
        */
    }


}
