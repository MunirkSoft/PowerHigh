package org.powerhigh.graphics;

import java.util.ArrayList;
import java.util.List;

// TODO Remove thread for GWT compatibility (only run thread when not in GWT environment?)
public class WindowEventThread extends Thread {

	private Interface win;
	private int targetFPS = 60;
	private int frames;
	private int fps;
	private boolean stop;

	private double delta;

	private long lastTick;

	private Runnable runnable = null;

	private List<Runnable> updateListeners = new ArrayList<Runnable>();

	public int getTargetFPS() {
		return targetFPS;
	}

	public void addUpdateListener(Runnable r) {
		updateListeners.add(r);
	}

	public double getDelta() {
		return delta;
	}

	public void setFrameRate(int frames) {
		this.targetFPS = frames;
	}

	public int getFPS() {
		return fps;
	}

	public WindowEventThread(Interface w) {
		win = w;
	}

	public void runLater(Runnable r) {
		runnable = r;
	}
	
	public void interrupt() {
		stop = true;
	}

	public void run() {
		setName("powerhigh-interface-thread");
		long sleepTime = 1000 / 60;
		while (!stop) {
			long estimatedTime = 0;
			if (targetFPS > 0) {
				estimatedTime = 1000 / targetFPS;
			}
			if (lastTick < System.currentTimeMillis()) {
				lastTick = System.currentTimeMillis() + 1000;
				fps = frames;
				frames = 0;
				delta = (double) targetFPS / (double) fps;
			}
			if (runnable != null) {
				runnable.run();
				runnable = null;
			}
			long start = System.currentTimeMillis();
			win.update();
			for (Runnable r : updateListeners) {
				r.run();
			}
			long end = System.currentTimeMillis();
			estimatedTime = end - start;
			sleepTime = (1000 - (estimatedTime - (1000 / targetFPS))) / targetFPS;
			if (sleepTime < 0) sleepTime = 0;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				interrupt();
			}
			frames++;
		}
		stop = false;
	}
}
