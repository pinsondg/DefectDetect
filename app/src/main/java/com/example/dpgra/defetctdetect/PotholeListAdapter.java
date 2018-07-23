package com.example.dpgra.defetctdetect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import model.Pothole;

public class PotholeListAdapter extends ArrayAdapter<Pothole> {

    private List<Pothole> list;

    public PotholeListAdapter(@NonNull Context context, List<Pothole> list) {
        this.list = list;
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Pothole pothole = getItem(position);

        if ( convertView == null ) {
            convertView = LayoutInflater.from(getContext()).inflate( R.layout.pothole, parent, false);
        }


        return super.getView(position, convertView, parent);
    }
}
