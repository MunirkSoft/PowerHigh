package org.powerhigh;

import java.awt.Rectangle;

import org.powerhigh.graphics.Interface;

public class RatioViewport extends ViewportManager {

	private int rW, rH;
	
	public RatioViewport(int rW, int rH) {
		this.rW = rW;
		this.rH = rH;
	}
	
	@Override
	public Rectangle getViewport(Interface win) {
		return new Rectangle((win.getWidth() / rW) * rW, (win.getHeight() / rH) * rH);
	}
	
}