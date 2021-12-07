import java.io.File;

import org.opencv.core.Core;

import common.OpenCVTools;
import processing.core.PApplet;
import processing.core.PImage;

public class ArucoMarkers extends PApplet {
	private PImage imageWinOriginal;

	public static void main(String[] args) {
		PApplet.main("ArucoMarkers");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public void settings() {
		size(800, 800, JAVA2D);
		noSmooth();
	}

	public void setup() {
	}

	public void draw() {
		background(100);

		if (imageWinOriginal != null)
			image(imageWinOriginal, 0, 0, 800, 800);
	}

	// This is a callback from file select window
	public void menuView_imageSelected(File selected) {
		if (selected == null) {
			System.out.println("Window was closed or the user hit cancel.");
		} else {
			System.out.println("User selected " + selected.getAbsolutePath());
			PImage newImage = loadImage(selected.getAbsolutePath());
			setOriginal(newImage);
			newImage = OpenCVTools.detectAruco(newImage);
			setOriginal(newImage);
		}
	}

	public void setOriginal(PImage img) {
		imageWinOriginal = img.copy();
	}

	public void keyPressed() {
		System.out.println("Key code pressed: " + keyCode);

		if (key == ' ') {
			selectInput("Select a file to process:", "menuView_imageSelected");
		} else if (key == '1') {
		} else if (key == 'q') { // lab
		} else if (keyCode == UP) {
		} else if (keyCode == DOWN) {
		} else if (keyCode == RIGHT) {
		} else if (keyCode == LEFT) {
		} else {
		}
	}

}
