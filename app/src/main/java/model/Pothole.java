package model;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Image;

import com.google.android.gms.maps.GoogleMap;

import org.opencv.core.Size;

import java.io.Serializable;

public class Pothole implements Serializable {

    public static final int SMALL_POTHOLE = 0;
    public static final int MEDIUM_POTHOLE = 1;
    public static final int LARGE_POTHOLE = 2;

    private Location location;
    private String id;
    private int size;


    /**
     * Constructor for creating a pothole object.
     *
     * @param location the location of the pothole
     * @param id the id number of the pothole
     * @param size the size of the pothole - Use the static finals of this class to get the sizes
     */
    public Pothole( Location location, String id, int size ) {
        this.location = location;
        this.id = id;
        this.size = size;
    }


    /**
     * Gets the size of the pothole.
     *
     * @return the size of the pothole
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the location of the pothole
     *
     * @return the location of the pothole
     */
    public Location getLocation() {
        return location;
    }


    /**
     * Gets the Id of the pothole.
     *
     * @return pothole id.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the street the pothole is on.
     * @return
     */

}
