package com.example.dpgra.defetctdetect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;

import model.Darknet;

public class CameraFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase mOpenCvCameraView;
    private Darknet net;
    private static CameraFragment cameraFragment;


    private CameraFragment() {
        super();
    }

    public static CameraFragment getInstance() {
        if ( cameraFragment == null ) {
            cameraFragment = new CameraFragment();
        }
        return cameraFragment;
    }

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getActivity()) {
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

    @Override
    public void onStart() {
        super.onStart();
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File fileDir = getActivity().getFilesDir();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_fragment, container, false);
        mOpenCvCameraView = (CameraBridgeViewBase) rootView.findViewById(R.id.CameraView);
        mOpenCvCameraView.setCvCameraViewListener(this);
        return rootView;
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

        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
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
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
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

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("Debug", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this.getActivity(), mLoaderCallback);
        } else {
            Log.d("Debug", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
