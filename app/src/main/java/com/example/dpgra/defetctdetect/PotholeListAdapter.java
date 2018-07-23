package com.example.dpgra.defetctdetect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import model.Pothole;

public class PotholeListAdapter extends ArrayAdapter<Pothole> {

    private List<Pothole> list;

    public PotholeListAdapter(@NonNull Context context, List<Pothole> list) {
        super(context, 0, list);
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
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
        return convertView;
    }

}
