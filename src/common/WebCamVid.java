package common;

import javax.swing.JOptionPane;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Capture;

public class WebCamVid {
	private final static WebCamVid instance = new WebCamVid();
	private PApplet parent;

	private Capture video;
	private int width = 0;
	private int height = 0;

	private WebCamVid() {
	}

	public final static WebCamVid getInstance() {
		return instance;
	}

	public void init(PApplet parent) {
		this.parent = parent;
	}

	public void openVideoStream() {
		String[] camListNames = null;

		try {
			video.stop();
		} catch (Exception e) {
			System.out.println("No active webcam to stop");
		}

		try {
			camListNames = Capture.list();
		} catch (Exception e) {
			System.out.println("No webcams available");
			return;
		}

		PApplet.printArray(Capture.list());

		String camName = (String) JOptionPane.showInputDialog(null,
				"Select the webcam.",
				"Select USB port",
				JOptionPane.PLAIN_MESSAGE,
				null,
				camListNames,
				0);

		if (camName != null) {
			video = new Capture(parent, camName);
			video.start();
			String[] parts = camName.split(",");
			parts = parts[1].split("=");
			parts = parts[1].split("x");
			width = Integer.parseInt(parts[0]);
			height = Integer.parseInt(parts[1]);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Capture getVideoStream() {
		return video;
	}

	public PImage getVideoImage() {
		if (video != null) {
			if (video.available() == true) {
				video.read();
			}
			return video.get();
		} else
			return null;
	}

//	private void sleep(int msec) {
//		try {
//			Thread.sleep(msec);
//		} catch (Exception e) {
//		}
//	}

	public void display(PGraphics painterWin, float zoom) {
		if (video != null) {
			if (video.available() == true) {
				video.read();
			}
			painterWin.beginDraw();
			painterWin.image(video, 0, 0, 176, 144);
			painterWin.endDraw();
		}
	}

}