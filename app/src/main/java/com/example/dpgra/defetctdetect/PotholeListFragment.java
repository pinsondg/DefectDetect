package com.example.dpgra.defetctdetect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import model.PotholeList;

public class PotholeListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter( new PotholeListAdapter(this.getActivity(), PotholeList.getInstance()));
        return rootView;
    }
}
