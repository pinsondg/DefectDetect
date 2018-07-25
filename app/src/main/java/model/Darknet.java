package model;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

/**
 * Represents the darknet DNN. Handles forwarding and creating a network.
 *
 * @author Daniel Pinson
 * @version 1.0
 */
public class Darknet {

	private static Dnn deepNetworkLoader;
	private Net network;
	private File cfg;
	private File model;

	/**
	 * Constructor.
	 *
	 * @param cfg the path to the cfg file
	 * @param model the path to the model file
	 */
	public Darknet(String cfg, String model) {
		this.cfg = new File( cfg );
		this.model = new File( model );
		deepNetworkLoader = new Dnn();
		network = deepNetworkLoader.readNetFromDarknet( cfg, model );
		for( String name : network.getLayerNames()) {
			System.out.println(name);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param cfg the cfg file
	 * @param model the model file
	 */
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

	/**
	 * Gets the network.
	 * @return the network
	 */
	public Net getNetwork() {
		return network;
	}

	/**
	 * Forwards the image passed through the network.
	 *
	 * @param image the image to forward
	 * @return the mat of outputs
	 */
	public Mat forwardLoadedNetwork(CameraBridgeViewBase.CvCameraViewFrame image ) {
		Mat newImg = image.rgba();
		return forwardLoadedNetwork(newImg);
	}

	/**
	 * Forwards the image passed through the network.
	 *
	 * @param image the image to forward
	 * @return the mat of outputs
	 */
	public Mat forwardLoadedNetwork( Mat image ) {

		image = Dnn.blobFromImage(image, .003922, new Size(448, 448),
			new Scalar(0,0,0) , false, false);
		long time1 = System.nanoTime();
		//image = Dnn.blobFromImage(image);
		network.setInput(image, "data");
		Mat retBlob = network.forward("detection_out");
		long time2 = System.nanoTime();
		System.out.println("Took " + (time2 - time1) * Math.pow(10, -9) + "s to forward network");
		return retBlob;
	}

}
