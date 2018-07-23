package com.example.dpgra.defetctdetect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import model.Pothole;
import model.PotholeList;

public class PotholeListAdapter extends ArrayAdapter<Pothole> {

    private List<Pothole> list;
    private PotholeListFragment fragment;

    public PotholeListAdapter(@NonNull Context context, List<Pothole> list, PotholeListFragment fragment) {
        super(context, 0, list);
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        if ( !list.isEmpty() ) {
            Pothole pothole = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.pothole, parent, false);
            }
            // Lookup view for data population
            TextView header = (TextView) convertView.findViewById(R.id.pothole_id);
            TextView coords = (TextView) convertView.findViewById(R.id.coords);
            TextView severity = (TextView) convertView.findViewById(R.id.severity);
            // Populate the data into the template view using the data object
            header.setText(pothole.getId());
            coords.setText("Coordinates: " + String.format("%.4f", pothole.getLat()) + ", " + String.format("%.4f", pothole.getLon()));
            severity.setText("Severity: " + pothole.getSize());
            // Return the completed view to render on screen
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    MapFragment.getInstance().setCustomLocation( new LatLng(list.get(position).getLat(), list.get(position).getLon()));
                    ((MainActivity)fragment.getActivity()).setToMapView();
                }
            });
        }
        return convertView;
    }

}
