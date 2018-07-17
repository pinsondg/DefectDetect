package model;


import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

import com.example.dpgra.defetctdetect.CameraFragment;
import com.example.dpgra.defetctdetect.MapFragment;

import java.io.File;
import java.io.IOException;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.videoio.VideoCapture;

public class Darknet {

	private static Dnn deepNetworkLoader;
	private Net network;
	private File cfg;
	private File model;
	
	public Darknet( String cfg, String model) {
		this.cfg = new File( cfg );
		this.model = new File( model );
		deepNetworkLoader = new Dnn();
		network = deepNetworkLoader.readNetFromDarknet( cfg, model );
		for( String name : network.getLayerNames()) {
			System.out.println(name);
		}
	}
	
	public Darknet( File cfg, File model ) {
		this.cfg = cfg;
		this.model = model;
		deepNetworkLoader = new Dnn();
		network = deepNetworkLoader.readNetFromDarknet(this.cfg.getAbsolutePath(),
				this.model.getAbsolutePath());
		for( String name : network.getLayerNames()) {
			System.out.println(name);
		}
	}
	
	public Net getNetwork() {
		return network;
	}


	public Mat forwardLoadedNetwork(CameraBridgeViewBase.CvCameraViewFrame image ) {
		Mat newImg = image.rgba();
		return forwardLoadedNetwork(newImg);
	}
	
	public Mat forwardLoadedNetwork( Mat image ) {
		image = Dnn.blobFromImage(image, .007843, new Size(448, 448),
			new Scalar(127.5, 127.5, 127.5) , true, false);
		long time1 = System.nanoTime();
		//image = Dnn.blobFromImage(image);
		network.setInput(image);
		Mat retBlob = network.forward();
		long time2 = System.nanoTime();
		System.out.println("Took " + (time2 - time1) * Math.pow(10, -9) + "s to forward network");
		return retBlob;
	}

}
