package com.example.dpgra.defectdetect;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Pothole;
import model.PotholeList;

@SuppressLint("NewApi")
public class PotholeListFragment extends Fragment implements View.OnClickListener, TextWatcher, AbsListView.OnScrollListener, Animator.AnimatorListener {

    private View rootView;
    private FloatingActionButton clearButton;
    private EditText editText;
    private int oldScrollY;
    private boolean isHidden = false;
    private boolean annimationEnded = true;
    private int origHeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list_fragment, container, false);
            try {
                editText = (EditText) rootView.findViewById(R.id.search_bar);
                //editText.setOnKeyListener(this);
                editText.addTextChangedListener(this);
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

    @SuppressLint("NewApi")
    private void createList(List<Pothole> potholeList) {
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        origHeight = listView.getHeight();
        listView.setAdapter( new PotholeListAdapter(this.getActivity(), potholeList, this));
        listView.setOnScrollListener(this);
        listView.setFastScrollEnabled(true);
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


    //@Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        //Log.i("TAG","KEY PRESSED");
        //Log.i("TAG", new Integer(keyEvent.getKeyCode()).toString());
        if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            //Log.i("TAG","ENNNTEEERRR PRESSED!");
             List<Pothole> results = search_for_string(editText.getText().toString());
            createList(results);
            ListView listView = rootView.findViewById(R.id.list_view);
            PotholeListAdapter adapter = (PotholeListAdapter) listView.getAdapter();
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //Log.i("TAG","KEY PRESSED");

        //Log.i("TAG","ENNNTEEERRR PRESSED!");
        List<Pothole> results = search_for_string(editText.getText().toString());
        createList(results);
        ListView listView = rootView.findViewById(R.id.list_view);
        PotholeListAdapter adapter = (PotholeListAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onPause() {
        editText.clearFocus();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        editText.clearFocus();
        super.onDestroy();
    }

    public EditText getEditText() {
        return editText;
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ((View)absListView.getParent()).getLayoutParams();
        //Animation a = new TranslateAnimation(0, 0, 0, -editText.getHeight() );
        //editText.setAnimation(a);
        if ( oldScrollY < i && !isHidden && annimationEnded) {
            ((View)editText.getParent()).animate().translationYBy( -editText.getHeight()).setDuration(300).setListener(this);
            params.topMargin = -editText.getMeasuredHeight();
            ((View)absListView.getParent()).setLayoutParams(params);
            isHidden = true;
        } else if ( oldScrollY > i && isHidden && annimationEnded ){
            ((View)editText.getParent()).animate().translationYBy( editText.getHeight()).setDuration(300).setListener(this);
            params.topMargin = 0;
            ((View)absListView.getParent()).setLayoutParams(params);
            isHidden = false;
        }
        oldScrollY = i;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        annimationEnded = false;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        annimationEnded = true;
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
