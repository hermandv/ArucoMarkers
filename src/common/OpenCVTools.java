package common;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import processing.core.PApplet;
import processing.core.PImage;

public class OpenCVTools {

	public static PImage bilateralFilter(PImage srcImage, int diameter, double sigmaColor, double sigmaSpace) {
		Mat sourceMat = toMap(srcImage);
		Mat dstMat = new Mat(srcImage.width, srcImage.height, CvType.CV_8UC3);

		Imgproc.bilateralFilter(sourceMat, dstMat, diameter, sigmaColor, sigmaSpace);
		return getSnapshot(dstMat);
	}

	public static PImage scaleDown(PImage srcImage, double scale) {
		Mat sourceMat = toMap(srcImage);
		Mat dstMat = new Mat((int) (srcImage.width * scale), (int) (srcImage.height * scale), CvType.CV_8UC3);
		Imgproc.resize(sourceMat, dstMat, new Size(), scale, scale, Imgproc.INTER_CUBIC);

		return getSnapshot(dstMat);
	}

	public static PImage scalePixelDownToBrushSize(PImage srcImage, double brushSize) {
		return scaleDown(srcImage, 1 / brushSize);
	}

	public static PImage filterCannyEdge1(PImage srcImage, int lowThreshold, int highThreshold) {
		Mat sourceMat = toMap(srcImage);
		Mat dstMat = new Mat(srcImage.width, srcImage.height, CvType.CV_8UC3);

		Imgproc.Canny(sourceMat, dstMat, lowThreshold, highThreshold);
		return getSnapshot(dstMat);
	}

	public static PImage clusterImg(PImage srcImage, int numColors) { // uses LAB color space to do clustering
		Mat sourceMat = toMap(srcImage);
		Mat sourceLAB = new Mat(srcImage.width, srcImage.height, CvType.CV_8UC3);

		Imgproc.cvtColor(sourceMat, sourceLAB, Imgproc.COLOR_RGB2Lab); // the source Mat is in RGB format
		List<Mat> dstMat = cluster(sourceLAB, numColors);
		Imgproc.cvtColor(dstMat.get(0), sourceLAB, Imgproc.COLOR_Lab2RGB);

		return getSnapshot(sourceLAB);
	}

	private static List<Mat> cluster(Mat cutout, int k) {
		Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
		Mat samples32f = new Mat();
		samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

		Mat labels = new Mat();
		TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
		Mat centers = new Mat();
		Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);
		return showClusters(cutout, labels, centers);
	}

	private static List<Mat> showClusters(Mat cutout, Mat labels, Mat centers) {
		centers.convertTo(centers, CvType.CV_8UC1, 255.0);
		centers.reshape(3);

		List<Mat> clusters = new ArrayList<Mat>();
		for (int i = 0; i < centers.rows() + 1; i++) { // get(0) contains the full image
			clusters.add(Mat.zeros(cutout.size(), cutout.type()));
		}

		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		for (int i = 0; i < centers.rows(); i++)
			counts.put(i, 0);

		int rows = 0;
		for (int y = 0; y < cutout.rows(); y++) {
			for (int x = 0; x < cutout.cols(); x++) {
				int label = (int) labels.get(rows, 0)[0];
				int r = (int) centers.get(label, 2)[0];
				int g = (int) centers.get(label, 1)[0];
				int b = (int) centers.get(label, 0)[0];
				counts.put(label, counts.get(label) + 1);
				clusters.get(label + 1).put(y, x, b, g, r);
				clusters.get(0).put(y, x, b, g, r);
				rows++;
			}
		}
		System.out.println(counts);
		return clusters;
	}

	public static PImage getSnapshot(Mat m) {
		PImage result = new PImage(m.width(), m.height(), PApplet.ARGB);
		toPImageCustom(m, result);
		return result;
	}

	private static void toPImageCustom(Mat m, PImage img) {
		img.loadPixels();

		if (m.channels() == 3) {
			Mat m2 = new Mat();
			Imgproc.cvtColor(m, m2, Imgproc.COLOR_RGB2RGBA);
			img.pixels = matToARGBPixels(m2);
		} else if (m.channels() == 1) {
			Mat m2 = new Mat();
			Imgproc.cvtColor(m, m2, Imgproc.COLOR_GRAY2RGBA);
			img.pixels = matToARGBPixels(m2);
		} else if (m.channels() == 4) {
			img.pixels = matToARGBPixels(m);
		}

		img.updatePixels();
	}

	private static int[] matToARGBPixels(Mat m) {
		int pImageChannels = 4;
		int numPixels = m.width() * m.height();
		int[] intPixels = new int[numPixels];
		byte[] matPixels = new byte[numPixels * pImageChannels];

		m.get(0, 0, matPixels);
		ByteBuffer.wrap(matPixels).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(intPixels);
		return intPixels;
	}

	public static Mat toMap(PImage srcImage) {
		Mat sourceARGB = new Mat(srcImage.height, srcImage.width, CvType.CV_8UC4);
		ImgToMap(srcImage, sourceARGB);

		Mat sourceBGRA = new Mat(srcImage.width, srcImage.height, CvType.CV_8UC4);
		ARGBtoBGRA(sourceARGB, sourceBGRA);
		Mat sourceRGB = new Mat(srcImage.width, srcImage.height, CvType.CV_8UC3);
		Imgproc.cvtColor(sourceBGRA, sourceRGB, Imgproc.COLOR_RGBA2RGB);

		return sourceRGB;
	}

	private static void ImgToMap(PImage img, Mat m) {
		BufferedImage image = (BufferedImage) img.getNative();
		int[] matPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		ByteBuffer bb = ByteBuffer.allocate(matPixels.length * 4);
		IntBuffer ib = bb.asIntBuffer();
		ib.put(matPixels);

		byte[] bvals = bb.array();

		m.put(0, 0, bvals);
	}

	private static void ARGBtoBGRA(Mat rgba, Mat bgra) {
		ArrayList<Mat> channels = new ArrayList<Mat>();
		Core.split(rgba, channels);

		ArrayList<Mat> reordered = new ArrayList<Mat>();
		// Starts as ARGB.
		// Make into BGRA.

		reordered.add(channels.get(3));
		reordered.add(channels.get(2));
		reordered.add(channels.get(1));
		reordered.add(channels.get(0));

		Core.merge(reordered, bgra);
	}

	public static PImage detectAruco(PImage inputFrame) {
		Mat image = toMap(inputFrame);
		Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50);
		Mat imageCopy = new Mat();
		image.copyTo(imageCopy);
		Mat ids = new Mat();
		List<Mat> corners = new ArrayList<>();

		Aruco.detectMarkers(image, dictionary, corners, ids);

		if (corners.size() > 0) {
			Aruco.drawDetectedMarkers(imageCopy, corners, ids);
		}

		return getSnapshot(imageCopy);
	}

	public static PImage detectArucoAndCrop(PImage inputFrame) {
		Mat image = toMap(inputFrame);
		Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50);
		Mat imageCopy = new Mat();
		image.copyTo(imageCopy);
		Mat ids = new Mat();
		List<Mat> corners = new ArrayList<>();

		Aruco.detectMarkers(image, dictionary, corners, ids);
		List<Point> cropCorners = new ArrayList<>();
		if (corners.size() > 0) {
			Aruco.drawDetectedMarkers(imageCopy, corners, ids);
		}

		for (int id = 0; id < 4; id++) {
			for (int i = 0; i < ids.size().height; i++) {
				if (ids.get(i, 0)[0] == (id + 1)) {
					cropCorners.add(new Point(corners.get(i).get(0, 0)));
				}
			}
		}

		System.out.println("IDs: " + ids);
		System.out.println("Corners: " + cropCorners);

		List<Point> target = new ArrayList<>();
		target.add(new Point(0, 0));
		target.add(new Point(90, 0));
		target.add(new Point(0, 90));
		target.add(new Point(90, 90));

		System.out.println("Target: " + target);

		if (cropCorners.size() != 4)
			return inputFrame;

		Mat trans = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(cropCorners), Converters.vector_Point2f_to_Mat(target));
		Imgproc.warpPerspective(image, imageCopy, trans, imageCopy.size());

		Rect rectCrop = new Rect(0, 0, 90, 90);
		imageCopy = new Mat(imageCopy, rectCrop);

		return getSnapshot(imageCopy);
	}

}
