package com.example.dpgra.defetctdetect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import android.util.Log;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;

import model.Darknet;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private Darknet net;
    private BottomNavigationView bottomNavigationView;
    private CameraBridgeViewBase mOpenCvCameraView;

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @SuppressLint("LongLogTag")
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.CameraView:
                    System.out.println("Selected camera");
                    return true;
                case R.id.map:
                    System.out.println("Selected map");
                    return true;
                case R.id.list:
                    System.out.println("Selected list");
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
       // mTextMessage = (TextView) findViewById(R.id.message);
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_dashboard);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.CameraView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        File fileDir = getFilesDir();
        System.out.println(fileDir.getAbsolutePath());
        File cfgFile = null;
        File weightsFile = null;
        for ( File file : fileDir.listFiles() ) {
            if ( file.getName().endsWith(".cfg") ) {
                cfgFile = file;
            } else if ( file.getName().endsWith(".weights") ) {
                weightsFile = file;
            }
            System.out.println(file.getAbsolutePath());
        }
        if ( cfgFile != null && weightsFile != null ) {
            net = new Darknet(cfgFile.getAbsolutePath(), weightsFile.getAbsolutePath());
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("Debug", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("Debug", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    /**
     * This method is invoked when camera preview has started. After this method is invoked
     * the frames will start to be delivered to client via the onCameraFrame() callback.
     *
     * @param width  -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.i("Status", "Started Camera Successfully");
    }

    /**
     * This method is invoked when camera preview has been stopped for some reason.
     * No frames will be delivered via onCameraFrame() callback after this method is called.
     */
    @Override
    public void onCameraViewStopped() {

    }

    /**
     * This method is invoked when delivery of the frame needs to be done.
     * The returned values - is a modified frame which needs to be displayed on the screen.
     * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
     *
     * @param inputFrame
     */
    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        Mat frame = inputFrame.rgba();
        if ( net != null ) {
            Mat retMat = net.forwardLoadedNetwork(inputFrame);
            for ( int i = 0; i < retMat.rows(); i++ ) {

                double confidence = retMat.get(i, 4)[0];
                if ( confidence > 25 ) {
                    double xCenter = retMat.get(i, 0)[0];
                    double yCenter = retMat.get(i, 1)[0];
                    double width = retMat.get(i, 2)[0];
                    double height = retMat.get(i, 4)[0];
                    Imgproc.rectangle(frame, new Point((xCenter - width / 2), (yCenter - height / 2 )), new Point(xCenter + width / 2
                            , yCenter + height / 2), new Scalar(255, 255, 0));
                }
            }
        } else {
            Log.i("System", "Problem forwarding network");
        }
        return frame;
    }
}
