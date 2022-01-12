import java.io.File;

import org.opencv.core.Core;

import common.OpenCVTools;
import common.WebCamVid;
import processing.core.PApplet;
import processing.core.PImage;

public class ArucoMarkers extends PApplet {
	private PImage imageWinOriginal;
	private PImage imageWinOutput;
	private final WebCamVid video = WebCamVid.getInstance();

	public static void main(String[] args) {
		PApplet.main("ArucoMarkers");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public void settings() {
		size(800, 800, JAVA2D);
		noSmooth();
	}

	public void setup() {
		WebCamVid.getInstance().init(this);
		video.openVideoStream();
	}

	public void draw() {
		background(100);

		if (imageWinOutput != null)
			image(imageWinOutput, 0, 0, 800, 800);
	}

	// This is a callback from file select window
	public void menuView_imageSelected(File selected) {
		if (selected == null) {
			System.out.println("Window was closed or the user hit cancel.");
		} else {
			System.out.println("User selected " + selected.getAbsolutePath());
			PImage newImage = loadImage(selected.getAbsolutePath());
			setOriginal(newImage);
			setOutput(newImage);
		}
	}

	public void setOriginal(PImage img) {
		imageWinOriginal = img.copy();
	}

	public void setOutput(PImage img) {
		imageWinOutput = img.copy();
	}

	public PImage getOriginal() {
		return imageWinOriginal;
	}

	public void keyPressed() {
		System.out.println("Key code pressed: " + keyCode);

		if (key == 'w') {
//			selectInput("Select a file to process:", "menuView_imageSelected");
			video.openVideoStream();
		} else if (key == '1') {
			PImage vidImg = WebCamVid.getInstance().getVideoImage();
			setOriginal(vidImg);
			PImage newImage = OpenCVTools.detectAruco(getOriginal());
			setOutput(newImage);
		} else if (key == '2') {
			PImage vidImg = WebCamVid.getInstance().getVideoImage();
			setOriginal(vidImg);
			PImage newImage = OpenCVTools.detectArucoAndCrop(getOriginal());
			setOutput(newImage);
		} else if (key == 'q') { // lab
		} else if (key == ' ') {
			PImage vidImg = WebCamVid.getInstance().getVideoImage();
			setOriginal(vidImg);
			setOutput(vidImg);

		} else if (keyCode == DOWN) {
		} else if (keyCode == RIGHT) {
		} else if (keyCode == LEFT) {
		} else {
		}
	}

}
