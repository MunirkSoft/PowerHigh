package org.lggl.graphics.renderers;

import java.awt.Graphics2D;

import org.lggl.graphics.Window;
import org.lggl.graphics.objects.GameObject;

public class SimpleRenderer implements IRenderer {

	private boolean pause;
	
	public SimpleRenderer() {
		
	}

	@Override
	public void render(Window win, Graphics2D g) {
		for (GameObject obj : win.getObjects()) {
			if (obj.isVisible()) {
				obj.paint(g, win);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean shouldRender(Window w, GameObject obj) {
		
		return true;
	}

	@Override
	public void unpause() {
		pause = false;
	}

	@Override
	public void pause() {
		pause = true;
	}

	@Override
	public boolean isPaused() {
		return pause;
	}

}