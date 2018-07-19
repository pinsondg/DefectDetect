package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public void overwrite( List<Pothole> list ) {
        this.clear();
        Iterator<Pothole> i = list.iterator();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

}
