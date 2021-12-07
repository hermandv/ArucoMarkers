package common;

import processing.core.PVector;

public class PVA {
	public PVector position;
	public PVector velocity;
	public PVector acceleration;
	public float tipHeight;

	public PVA(PVector pos, PVector vel, PVector acc, float tipHeight) {
		this.position = pos;
		this.velocity = vel;
		this.acceleration = acc;
		this.tipHeight = tipHeight;
	}
	
	public float getX() {
		return position.x;
	}
	public float getY() {
		return position.y;
	}

}