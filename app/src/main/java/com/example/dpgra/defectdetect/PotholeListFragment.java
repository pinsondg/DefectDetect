package com.example.dpgra.defectdetect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Pothole;
import model.PotholeList;

public class PotholeListFragment extends Fragment implements View.OnClickListener, View.OnKeyListener {

    private View rootView;
    private FloatingActionButton clearButton;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list_fragment, container, false);
            try {
                editText = (EditText) rootView.findViewById(R.id.search_bar);
                editText.setOnKeyListener(this);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        createList(PotholeList.getInstance());
        clearButton = rootView.findViewById(R.id.floatingActionButton);
        clearButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        createList(PotholeList.getInstance());
        super.onResume();

    }

    private void createList(List<Pothole> potholeList) {
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter( new PotholeListAdapter(this.getActivity(), potholeList, this));
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




    public List<Pothole> search_for_string(String str) {
         List<Pothole> new_list = PotholeList.getInstance();
        str = str.toLowerCase();
        if (!str.isEmpty()) {
            new_list = new ArrayList<Pothole>();
            Iterator<Pothole> i = PotholeList.getInstance().iterator();
            while (i.hasNext()) {
                Pothole temp = i.next();
                String LatAsString = String.format("%.4f",new Double(temp.getLat()));
                String LongAsString = String.format("%.4f",new Double(temp.getLon()));
                if (temp.getId().contains(str)) {
                    new_list.add(temp);
                } else if (LatAsString.contains(str)) {
                    new_list.add(temp);
                } else if (LongAsString.contains(str)) {
                    new_list.add(temp);
                }
            }
        }
        return new_list;
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        System.out.print("KEY PRESSED");
        System.out.print(keyEvent.getKeyCode());
        if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            System.out.print("ENNNTEEERRR PRESSED!");
             List<Pothole> results = search_for_string(editText.getText().toString());
            createList(results);
            ListView listView = rootView.findViewById(R.id.list_view);
            PotholeListAdapter adapter = (PotholeListAdapter) listView.getAdapter();
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }
}
