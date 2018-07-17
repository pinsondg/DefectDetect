package model;

import java.util.ArrayList;

/**
 * A list of potholes. Uses the singleton design structure so the list can be accessed easily
 * across different classes.
 */
public class PotholeList extends ArrayList<Pothole> {

    private static PotholeList potholeList;

    private PotholeList() {
        super(0);
    }

    public static PotholeList getInstance() {
        if ( potholeList == null ) {
            potholeList = new PotholeList();
        }
        return potholeList;
    }

}
