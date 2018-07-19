package model;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Image;

import java.io.Serializable;

public class Pothole implements Serializable {

    public static final int SMALL_POTHOLE = 0;
    public static final int MEDIUM_POTHOLE = 1;
    public static final int LARGE_POTHOLE = 2;

    private Location location;
    private String id;
    private int size;


    public Pothole( Location location, String id, int size ) {

    }

}
