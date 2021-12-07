package common;

import java.awt.Color;
import java.io.File;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public final class Utils {
	// Example Utility method

	/**
	 * addImgToWindowQ - place an image in a specific quarter of a window.
	 *
	 * @param img       Source image
	 * @param window    Destination image
	 * @param quaterNum 1=TL , 2=TR , 3=BL , 4=BR, anything else fill the entire
	 *                  window
	 */
	public static void addImgToWindowQ(PGraphics img, PGraphics window, int quaterNum) {
		addImgToWindowQ((PImage) img, window, quaterNum);
	}

	public static void addImgToWindowQ(PImage img, PGraphics window, int quaterNum) {
		int x, y, w, h;

		if (img == null)
			return;

		w = (int) window.width / 2;
		h = (int) window.height / 2;

		if (quaterNum == 1) {
			x = 0;
			y = 0;
		} else if (quaterNum == 2) {
			x = w;
			y = 0;
		} else if (quaterNum == 3) {
			x = 0;
			y = h;
		} else if (quaterNum == 4) {
			x = w;
			y = h;
		} else {
			x = 0;
			y = 0;
			w = (int) window.width;
			h = (int) window.height;
		}
		window.beginDraw();
		window.image(img, x, y, w, h);
		window.endDraw();
	}

	public static void clearGraphics(PGraphics window, int col) {
		window.beginDraw();
		window.stroke(0);
		window.background(col);
		window.endDraw();
	}

	public static int grayToRGB(int gray) {
		return Color.HSBtoRGB(0, 0, (float) (gray / 255f));
	}

	public final static float red(int rgb) {
		float c = (rgb >> 16) & 0xff;
		return c;
	}

	public final static float green(int rgb) {
		float c = (rgb >> 8) & 0xff;
		return c;
	}

	public final static float blue(int rgb) {
		float c = (rgb) & 0xff;
		return c;
	}

	public final static float alpha(int rgb) {
		float c = (rgb >> 24) & 0xff;
		return c;
	}

	public final static int hue(int color) {
		float[] hsbVals = new float[3];
		Color c = new Color(color);
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbVals);
		return (int) (hsbVals[0] * 255);
	}

	public final static int sat(int color) {
		float[] hsbVals = new float[3];
		Color c = new Color(color);
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbVals);
		return (int) (hsbVals[1] * 255);
	}

	public final static int bri(int color) {
		float[] hsbVals = new float[3];
		Color c = new Color(color);
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbVals);
		return (int) (hsbVals[2] * 255);
	}

	public final static int lum(int color) {
		float[] hsbVals = new float[3];
		Color c = new Color(color);
		hsbVals = rgbToHsl((int) c.getRed(), (int) c.getGreen(), (int) c.getBlue());
		return (int) (hsbVals[2] * 255);
	}

	public static float[] rgbToHsl(int pR, int pG, int pB) {
		float r = pR / 255f;
		float g = pG / 255f;
		float b = pB / 255f;

		float max = (r > g && r > b) ? r : (g > b) ? g : b;
		float min = (r < g && r < b) ? r : (g < b) ? g : b;

		float h, s, l;
		l = (max + min) / 2.0f;

		if (max == min) {
			h = s = 0.0f;
		} else {
			float d = max - min;
			s = (l > 0.5f) ? d / (2.0f - max - min) : d / (max + min);

			if (r > g && r > b)
				h = (g - b) / d + (g < b ? 6.0f : 0.0f);

			else if (g > b)
				h = (b - r) / d + 2.0f;

			else
				h = (r - g) / d + 4.0f;

			h /= 6.0f;
		}

		float[] hsl = { h, s, l };
		return hsl;
	}

	public final static int color(int r, int g, int b) {
		int c = ((r << 16) & 0x00ff0000) + ((g << 8) & 0x0000ff00) + ((b) & 0x000000ff);
		return c;
	}

	public final static int color(int r, int g, int b, int alpha) {
		int c = ((alpha << 24) & 0xff000000) + ((r << 16) & 0x00ff0000) + ((g << 8) & 0x0000ff00) + ((b) & 0x000000ff);
		return c;
	}

	public final static int color(float r, float g, float b) {
		return color((int) r, (int) g, (int) b);
	}

	public final static int getAvgImgColor(PImage img) {
		img.loadPixels();
		int r = 0, g = 0, b = 0;
		for (int i = 0; i < img.pixels.length; i++) {
			int c = img.pixels[i];
			r += c >> 16 & 0xFF;
			g += c >> 8 & 0xFF;
			b += c & 0xFF;
		}
		r /= img.pixels.length;
		g /= img.pixels.length;
		b /= img.pixels.length;
		return color(r, g, b);
	}

	public final static float colorDistance(int a, int b) {
		PVector d = new PVector(Utils.red(a), Utils.green(a), Utils.blue(a));
		d.sub(Utils.red(b), Utils.green(b), Utils.blue(b));
		return d.mag();
	}

	public final static boolean isBnW(int color) {
		float[] hsbVals = new float[3];
		Color c = new Color(color);
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbVals);
		float sat = hsbVals[1] * 255f;
		float bright = hsbVals[2] * 255f;
		if (((sat <= 10f)) || (bright <= 20f))
			return true;

		return false;
	}

	public final static boolean isWhite(int color) {
		float[] hsbVals = new float[3];
		Color c = new Color(color);
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbVals);
		float sat = hsbVals[1] * 255f;
		float bright = hsbVals[2] * 255f;
		if ((sat <= 10f) && (bright >= 250f))
			return true;
		return false;
	}

	public final static int hueDiff(int color1, int color2) {
		int hueDiff = hue(color1) - hue(color2);
		if (hueDiff < 0)
			hueDiff = hueDiff * (-1);
		return hueDiff;
	}

	public final static int hueShortDistance(int currentHue, int targetHue) {
		int distHue;
		int movesInc = distanceToTheRight(currentHue, targetHue);
		int movesDec = distanceToTheLeft(currentHue, targetHue);

		if (movesInc == movesDec) { // is to the right of target - add left spot color
			distHue = 0;
		} else if (movesInc < movesDec) { // is to the right of target - add left spot color
			distHue = movesInc;
		} else {
			distHue = -movesDec;
		}
		return distHue;
	}

	public final static int distanceToTheRight(int current, int target) {
		int movesInc = (target - current) + 1 + ((target > current) ? 0 : 255);
		return movesInc;
	}

	public final static int distanceToTheLeft(int current, int target) {
		int movesDec = (current - target) + 1 + ((target < current) ? 0 : 255);
		return movesDec;
	}

	public final static String getIndent(int indentCount) {
		return new String(new char[indentCount]).replace('\0', ' ');
	}

	/*
	 * Averages the pixels in a given image (img) within a circular region centered
	 * at (x, y) with a radius of 'radius' pixels. This function uses the "simple"
	 * approach to average RGB colors which simply returns the mean of the red,
	 * green, and blue components.
	 */
	public final static int getAverageRGBCircle(PImage img, int x, int y, int radius) {
		float r = 0;
		float g = 0;
		float b = 0;
		int num = 0;
		/* Iterate through a bounding box in which the circle lies */
		for (int i = x - radius; i < x + radius; i++) {
			for (int j = y - radius; j < y + radius; j++) {
				/* If the pixel is outside the canvas, skip it */
				if (i < 0 || i >= img.width || j < 0 || j >= img.height)
					continue;

				/* If the pixel is outside the circle, skip it */
				if (PApplet.dist(x, y, i, j) > radius)
					continue;

				/* Get the color from the image, add to a running sum */
				int c = img.pixels[i + j * img.width];
//				r += red(c) * red(c);
//				g += green(c) * green(c);
//				b += blue(c) * blue(c);
				r += red(c);
				g += green(c);
				b += blue(c);
				num++;
			}
		}
		/* Return the mean of the R, G, and B components */
//		return color(PApplet.sqrt(r / num), PApplet.sqrt(g / num), PApplet.sqrt(b / num));
		return color((r / num), (g / num), (b / num));
	}

	public static float runExternalCmd(String cmdFilename, String arguments) {
		String pathData = getDataPath();
		float error = 0;

		Process cmdProcess;
		try {
			String cmdString = "cmd /c start /min " + pathData + cmdFilename + " " + arguments;
//			System.out.println(cmdString);

			cmdProcess = Runtime.getRuntime().exec(cmdString);
			cmdProcess.waitFor();
			error = cmdProcess.exitValue();
		} catch (Exception e) {
			System.out.println("Error running external cmd: " + e.getMessage());
		}
		return error;
	}

	public static String getDataPath() {
		return System.getProperty("user.dir") + "\\data\\";
	}

	public static boolean fileExist(String fileName, int timeOut100Msecs) {
		// Get the file
		File f = new File(fileName);
		int timeCount = 0;
		while (timeCount * 100 < timeOut100Msecs) {
//			System.out.print(":");
			if (f.exists())
				return true;
			sleep(100);
			timeCount++;
		}
		System.out.println("Error file not found: " + fileName);
		return false;
	}

	public static boolean fileReadable(String fileName, int timeOut100Msecs) {
		// Get the file
		File f = new File(fileName);
		int timeCount = 0;
		while (timeCount * 100 < timeOut100Msecs) {
//			System.out.print(":");
			sleep(100);
			if (f.canRead() && f.canWrite() && f.canExecute())
				return true;
			sleep(100);
			timeCount++;
		}
		System.out.println("Error file not found: " + fileName);
		return false;
	}

	public static void sleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (Exception e) {
		}
	}

}
