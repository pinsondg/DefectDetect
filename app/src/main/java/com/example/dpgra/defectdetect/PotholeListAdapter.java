package com.example.dpgra.defectdetect;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import model.Pothole;
import model.PotholeList;

/**
 * The adapter for the pothole list. Transforms each item in the pothole list into a view that can
 * be used by the list view.
 *
 * @author Daniel Pinson, Vamsi Yadav
 * @version 1.0
 */
public class PotholeListAdapter extends ArrayAdapter<Pothole> {

    private List<Pothole> list;
    private PotholeListFragment fragment;

    /**
     * Constructor for the pothole list adapter.
     *
     * @param context context
     * @param list the list to create
     * @param fragment the fragment of the list view
     */
    public PotholeListAdapter(@NonNull Context context, List<Pothole> list, PotholeListFragment fragment) {
        super(context, 0, list);
        this.list = list;
        this.fragment = fragment;
    }

    /**
     * Turns the items of a list into the view items of the pothole list.
     * @param position the position in the list
     * @param convertView the return view
     * @param parent the parent view group
     * @return the view to display
     */
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        if ( !list.isEmpty() ) {
            final Pothole pothole = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.pothole, parent, false);
            }
            Button button = convertView.findViewById(R.id.del_button);
            LinearLayout linearLayout = convertView.findViewById(R.id.liner_layout);
            SlidingSpringAnimation animation = new SlidingSpringAnimation(button, linearLayout, SlidingSpringAnimation.RIGHT_TO_LEFT, fragment, pothole);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ( list instanceof PotholeList ) {
                        list.remove(position);
                    } else {
                        list.remove(position);
                        PotholeList.getInstance().remove(pothole);
                    }
                    notifyDataSetChanged();
                }
            });
            linearLayout.setOnTouchListener(animation);
            this.registerDataSetObserver(animation);
            // Lookup view for data population
            TextView header = (TextView) convertView.findViewById(R.id.pothole_id);
            TextView coords = (TextView) convertView.findViewById(R.id.coords);
            TextView severity = (TextView) convertView.findViewById(R.id.severity);
            // Populate the data into the template view using the data object
            header.setText(pothole.getId());
            coords.setText("Lat: " + String.format("%.4f", pothole.getLat()) + ", Lon: " + String.format("%.4f", pothole.getLon()));
            severity.setText("Severity: " + pothole.getSize());
            // Return the completed view to render on screen
        }
        return convertView;
    }

}
