package model;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Image;

import java.io.Serializable;

public class Pothole implements Serializable {

    private Location location;
    private Image image;

    public Pothole() {

    }

}
