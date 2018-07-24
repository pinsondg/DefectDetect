package com.example.dpgra.defetctdetect;

import android.database.DataSetObserver;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import model.Pothole;

public class SlidingSpringAnimation extends DataSetObserver implements View.OnTouchListener {

    public static final int RIGHT_TO_LEFT = -1;
    public static final int LEFT_TO_RIGHT = 1;
    private int direction;
    private boolean isRevealed;
    private View button;
    private View view;
    private float x1;
    private float x2;
    private Fragment fragment;
    private Pothole pothole;

    public SlidingSpringAnimation(View button, View view, int direction, Fragment fragment, Pothole pothole) {
        this.x1 = 0;
        this.x2 = 0;
        this.button = button;
        this.direction = direction;
        isRevealed = false;
        this.fragment = fragment;
        this.pothole = pothole;
        this.view = view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.view = view;
        if ( !isRevealed ) {
            SpringAnimation animation = new SpringAnimation(view, DynamicAnimation.TRANSLATION_X,
                    direction*(this.button.getMeasuredWidth()));
            switch(motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    x1 = motionEvent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = motionEvent.getX();
                    float deltaX = x2 - x1;
                    if (Math.abs(deltaX) > this.button.getMeasuredWidth())
                    {
                        if ( x2 < x1 ) {
                            animation.start();
                            isRevealed = true;
                        } else {
                            sendBack( view );
                        }
                    }
                    else {
                        MapFragment.getInstance().setCustomLocation(new LatLng(pothole.getLat(), pothole.getLon()));
                        ((MainActivity)fragment.getActivity()).setToMapView();
                        sendBack(view);
                    }
                    break;
            }
        }
        else {
            sendBack( view );
        }
        return true;
    }

    public void sendBack( View view ) {
        SpringAnimation animation = new SpringAnimation(view, DynamicAnimation.TRANSLATION_X,
                0);
        animation.start();
        isRevealed = false;
        x1 = 0;
        x2 = 0;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    @Override
    public void onChanged() {
        sendBack(view);
    }
}
