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

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list_fragment, container, false);
        createList();
        return rootView;
    }

    @Override
    public void onResume() {
        createList();
        super.onResume();

    }

    private void createList() {
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter( new PotholeListAdapter(this.getActivity(), PotholeList.getInstance()));
    }

}
