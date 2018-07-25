package com.example.dpgra.defectdetect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import model.PotholeList;

public class PotholeListFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private FloatingActionButton clearButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list_fragment, container, false);
        createList();
        clearButton = rootView.findViewById(R.id.floatingActionButton);
        clearButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        createList();
        super.onResume();

    }

    private void createList() {
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter( new PotholeListAdapter(this.getActivity(), PotholeList.getInstance(), this));
    }

    @Override
    public void onClick(View view) {
        if ( view.getId() == rootView.findViewById(R.id.floatingActionButton).getId() ) {
            new AlertDialog.Builder(this.getContext())
                    .setTitle("Alert")
                    .setMessage("Do you want to clear the list?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    PotholeList.getInstance().clear();
                                    ListView listView = rootView.findViewById(R.id.list_view);
                                    PotholeListAdapter adapter = (PotholeListAdapter) listView.getAdapter();
                                    adapter.clear();
                                    adapter.notifyDataSetChanged();
                                }
                            }).setNegativeButton("No", null).show();
        }
    }
}
