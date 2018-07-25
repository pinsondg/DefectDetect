package model;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Image;

import com.google.android.gms.maps.GoogleMap;

import org.opencv.core.Size;

import java.io.Serializable;

/**
 * Represents a pothole object.
 *
 * @author Daniel Pinson, Vamsi Yadav
 * @version 1.0
 */
public class Pothole implements Serializable {

    public static final int SMALL_POTHOLE = 0;
    public static final int MEDIUM_POTHOLE = 1;
    public static final int LARGE_POTHOLE = 2;

    private double lon;
    private double lat;
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
        this.lon = location.getLongitude();
        this.lat = location.getLatitude();
        this.id = id;
        this.size = size;
    }

    public Pothole( double longitude, double latitude, String id, int size ) {
        this.lat = latitude;
        this.lon = longitude;
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

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    /**
     * Gets the Id of the pothole.
     *
     * @return pothole id.
     */
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if ( o instanceof Pothole ) {
            double oLat = ((Pothole) o).getLat();
            double oLon = ((Pothole) o).getLon();
            int oSize = ((Pothole) o).getSize();

            if ( oLat == lat && oLon == lon && oSize == size ) {
                return true;
            }
        }
        return false;
    }

}
