package com.example.dpgra.defectdetect;

import android.animation.Animator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import model.Pothole;
import model.PotholeList;

public class DeleteAnimation implements View.OnClickListener, Animator.AnimatorListener {


    private ListView view;
    private List<Pothole> list;
    private int position;

    public DeleteAnimation(ListView view, List<Pothole> list, int position ) {
        this.list = list;
        this.view = view;
        this.position = position;
    }
    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        ((ArrayAdapter<Pothole>)view.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    @Override
    public void onClick(View view) {
        if ( list instanceof PotholeList ) {
            ((View)view.getParent().getParent()).animate().alpha(0).setDuration(200).setListener(this);
            list.remove(position);
            animateUp();
        } else {
            ((View)view.getParent().getParent()).animate().alpha(0).setDuration(200).setListener(this);
            list.remove(position);
            PotholeList.getInstance().remove(this.view.getAdapter().getItem(position));
            animateUp();
        }
    }

    private void animateUp() {
        if ( position != list.size() - 2 ) {
            for ( int i = position + 1; i < list.size() - 1; i++ ) {
                getViewByPosition(i).animate().translationYBy(-view.getHeight()).setDuration(300).setListener(this);
            }
        } else {
            ((ArrayAdapter<Pothole>)view.getAdapter()).notifyDataSetChanged();
        }
    }

    private View getViewByPosition(int pos) {
        final int firstListItemPosition = view.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + view.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return view.getAdapter().getView(pos, null, view);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return view.getChildAt(childIndex);
        }
    }
}
