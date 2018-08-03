package com.example.dpgra.defectdetect;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Pothole;
import model.PotholeList;

@SuppressLint("NewApi")
public class PotholeListFragment extends Fragment implements TextWatcher, AbsListView.OnScrollListener, Animator.AnimatorListener {

    private View rootView;
    private EditText editText;
    private int oldScrollY;
    private boolean isHidden = false;
    private boolean animationEnded = true;
    private List<Pothole> new_list = new ArrayList<Pothole>();
    //private int origHeight;
    //private float y0;

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
        rootView.findViewById(R.id.more_button).setOnClickListener(new MoreMenuHandler(this, rootView));
        createList(PotholeList.getInstance());
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
        //origHeight = listView.getHeight();
        listView.setAdapter( new PotholeListAdapter(this.getActivity(), potholeList, this));
        listView.setOnScrollListener(this);
        listView.setFastScrollEnabled(true);
    }


    public List<Pothole> getNew_list() {
        return new_list;
    }

    public List<Pothole> search_for_string(String str) {
        new_list = PotholeList.getInstance();
        str = str.toLowerCase();
        if (!str.isEmpty()) {
            new_list = new ArrayList<Pothole>();
            Iterator<Pothole> i = PotholeList.getInstance().iterator();
            while (i.hasNext()) {
                Pothole temp = i.next();
                String LatAsString = String.format("%.4f",new Double(temp.getLat()));
                String LongAsString = String.format("%.4f",new Double(temp.getLon()));
                String Severity = new Integer(temp.getSize()).toString();
                if (temp.getId().contains(str)) {
                    new_list.add(temp);
                } else if (LatAsString.contains(str)) {
                    new_list.add(temp);
                } else if (LongAsString.contains(str)) {
                    new_list.add(temp);
                } else if (Severity.matches(str)) {
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
        if ( oldScrollY < i && !isHidden && animationEnded) {
            ((View)editText.getParent()).animate().translationYBy( -editText.getHeight()).setDuration(300).setListener(this);
            ((View)absListView.getParent()).animate().yBy(-editText.getHeight()).setDuration(300);
            params.bottomMargin = -editText.getMeasuredHeight();
            ((View)absListView.getParent()).setLayoutParams(params);
            isHidden = true;
        } else if ( oldScrollY > i && isHidden && animationEnded ){
            ((View)editText.getParent()).animate().translationYBy( editText.getHeight()).setDuration(300).setListener(this);
            ((View)absListView.getParent()).animate().yBy(editText.getHeight()).setDuration(300);
            ((View)absListView.getParent()).setLayoutParams(params);
            isHidden = false;
        }
        oldScrollY = i;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        animationEnded = false;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        animationEnded = true;
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    /* Experiment
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ((View)view.getParent()).getLayoutParams();
        if ( !isHidden ) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y0 = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float y = motionEvent.getY();
                    if ( ((View)view.getParent()).getY() >= 0 && y < y0 ) {
                        System.out.println(y);
                        ((View)editText.getParent()).animate().translationYBy( y - y0 ).setDuration(0);
                        ((View)view.getParent()).animate().yBy( y - y0 ).setDuration(0);
                        //params.bottomMargin = -editText.getMeasuredHeight();
                        //((View)view.getParent()).setLayoutParams(params);
                    } else if ( view.getY() <= 0 && y > y0 ) {
                        ((View)editText.getParent()).animate().translationYBy(y - y0).setDuration(0);
                        ((View)view.getParent()).animate().yBy(y - y0).setDuration(0);
                    }
                    y0 = y;
                    break;
            }
        }
        return false;
    }
    */
}
