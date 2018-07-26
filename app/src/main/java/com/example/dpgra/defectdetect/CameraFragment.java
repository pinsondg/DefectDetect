package com.example.dpgra.defectdetect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import model.Darknet;
import model.Pothole;
import model.PotholeList;

import static org.opencv.android.BaseLoaderCallback.TAG;

/**
 * The fragment that shows the camera feed and detects the potholes.
 *
 * @author Daniel Pinson, Vamsi Yadav
 * @version 1.0
 */
public class CameraFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase mOpenCvCameraView;
    private Darknet net;
    private static CameraFragment cameraFragment;
    private PotholeList potholeList;
    private Integer OrientationIsValid;


    @SuppressLint("ValidFragment")
    private CameraFragment() {
        super();
        potholeList = potholeList.getInstance();
        OrientationIsValid = 2;
    }

    /**
     * Returns the one instance of the camera fragment.
     *
     * @return the camera fragment
     */
    public static CameraFragment getInstance() {
        if ( cameraFragment == null ) {
            cameraFragment = new CameraFragment();
        }
        return cameraFragment;
    }

    /**
     * Makes sure opencv can load successfully.
     */
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

        if(OrientationIsValid == 2) {
            new AlertDialog.Builder(this.getContext())
                    .setTitle("Alert")
                    .setMessage("Do you want to keep this orientation?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    OrientationIsValid = 0;
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    OrientationIsValid = 1;
                }
            }).show();
        }

        AssetManager assetManager = getResources().getAssets();
        String cfgFile = getPath("yolov2-tiny2.cfg", this.getActivity());
        String weightsFile = getPath("yolov2-tiny2_36500.weights", this.getActivity());


        if ( cfgFile != null && weightsFile != null ) {
            net = new Darknet( cfgFile, weightsFile );
        }
        //mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
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
        Mat mRgbaT = frame;
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);
        if(OrientationIsValid == 1) {
            mRgbaT = frame.t();
            Core.flip(frame.t(), mRgbaT, 1);
            Imgproc.resize(mRgbaT, mRgbaT, frame.size());
        }

        int cols = mRgbaT.cols();
        int rows = mRgbaT.rows();
        Size cropSize;
        if ((float)cols / rows > 1) {
            cropSize = new Size(rows * 1, rows);
        } else {
            cropSize = new Size(cols, cols / 1);
        }
        int y1 = (int)(rows - cropSize.height) / 2;
        int y2 = (int)(y1 + cropSize.height);
        int x1 = (int)(cols - cropSize.width) / 2;
        int x2 = (int)(x1 + cropSize.width);
        Mat subFrame = mRgbaT.submat(y1, y2, x1, x2);
        cols = subFrame.cols();
        rows = subFrame.rows();
        int sevarity = 0;

        if ( net != null ) {
            Mat retMat = net.forwardLoadedNetwork(mRgbaT);
            for ( int i = 0; i < retMat.rows(); i++ ) {
                double confidence = retMat.get(i, 5)[0];
                if ( confidence > 0.60 ) {
                    printMat(retMat.row(i));
                    System.out.println("YESSSSSS");
                    double xCenter = retMat.get(i, 0)[0]*cols;
                    double yCenter = retMat.get(i, 1)[0]*rows;
                    double width = retMat.get(i, 2)[0]*cols;
                    double height = retMat.get(i, 3)[0]*rows;
                    Imgproc.rectangle(subFrame, new Point((xCenter - width / 2), (yCenter - height / 2 )), new Point(xCenter + width / 2
                            , yCenter + height / 2), new Scalar(255, 0, 0), 10);
                    sevarity++;
                }
            }
            if ( sevarity > 0 ) {
                createPothole(sevarity);
            }
        } else {
            Log.i("System", "Problem forwarding network");
        }
        return mRgbaT;
    }

    private Pothole createPothole( int sevatity ) {
        Location location = ((MainActivity) this.getActivity()).getLocation();
        Pothole pothole = null;
        if ( location != null ) {
            pothole = new Pothole( location, createPotholeId(), sevatity);
            addToPotholeList(pothole);
        }
        return pothole;
    }

    /**
     * Creates the id string for a pothole.
     *
     * @return the pothole id
     */
    private String createPotholeId() {
        PotholeList list = PotholeList.getInstance();
        int num = 0;
        if ( !list.isEmpty() ) {
            String lastId = list.get(list.size() - 1).getId();
            num = Integer.parseInt(lastId.substring(1));
        }
        String retId = "p" + (num + 1);
        return retId;
    }

    /**
     * Prints the returned matrix outputted from the network.
     * @param mat the mat to print
     */
    private void printMat( Mat mat ) {
        for ( int i = 0; i < mat.rows(); i++) {
            System.out.print("[ ");
            for ( int n = 0; n < mat.cols(); n++ ) {
                try {
                    System.out.printf( "%.2f ", mat.get(i, n)[0]);
                } catch (NullPointerException e) {
                    System.out.print( " " );
                }

            }
            System.out.println("]");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!OpenCVLoader.initDebug()) {
                Log.d("Debug", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this.getActivity(), mLoaderCallback);
            } else {
                Log.d("Debug", "OpenCV library found inside package. Using it!");
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        } catch(NullPointerException e) {
            Toast.makeText(this.getActivity(), "OpenCV is required to run this feature. Please install OpenCV from the Google Play Store or download the APK from their official website.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Gets the path of a file and create it in the app memory.
     *
     * @param file the file to find
     * @param context context
     * @return the pathname
     */
    @SuppressLint("LongLogTag")
    private static String getPath(String file, Context context) {
        AssetManager assetManager = context.getAssets();

        BufferedInputStream inputStream = null;
        try {
            // Read data from assets.
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // Return a path to file which may be read in common way.
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i(TAG, "Failed to upload a file");
        }
        return "";
    }

    /**
     * Adds the pothole to the pothole list.
     *
     * @param pothole the pothole to add
     */
    public void addToPotholeList(Pothole pothole) {
        potholeList.add(pothole);
    }
}
