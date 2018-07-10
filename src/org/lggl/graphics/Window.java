package org.lggl.graphics;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.lggl.Camera;
import org.lggl.ViewportManager;
import org.lggl.objects.Container;
import org.lggl.objects.GameObject;
import org.lggl.graphics.renderers.IRenderer;
import org.lggl.graphics.renderers.lightning.Lightning;
import org.lggl.input.Keyboard;
import org.lggl.input.Mouse;

/**
 * 
 *
 */
public class Window {

	private JFrame win = new JFrame();
	private int width = 640, height = 480;
	private String title;

	private Keyboard input = new Keyboard(this);
	private Mouse mouse = new Mouse(-1, -1, this);
	private boolean fullscreen;
	private ArrayList<GameObject> objects = new ArrayList<GameObject>();
	private GameObject focusedObj;
	private WindowEventThread thread = new WindowEventThread(this);
	private WindowPanel panel = new WindowPanel(this);
	private ViewportManager viewport;
	private Graphics customGraphics;
	
	private Container objectContainer;
	
	private Camera camera;

	private int vW, vH;
	private boolean legacyFullscreen = false;

	public boolean isLegacyFullscreen() {
		return legacyFullscreen;
	}
	
	public Container getObjectContainer() {
		return objectContainer;
	}

	public void setLegacyFullscreen(boolean legacyFullscreen) {
		this.legacyFullscreen = legacyFullscreen;
	}

	public Graphics getCustomGraphics() {
		return customGraphics;
	}

	public void setCustomGraphics(Graphics customGraphics) {
		this.customGraphics = customGraphics;
	}

	private static IRenderer render;

	public static IRenderer getRenderer() {
		return render;
	}

	public static void setRenderer(IRenderer render) {
		Window.render = render;
	}

	static {
		try {
			if (true)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

	}

	public boolean shouldRender(GameObject obj) {
		return render.shouldRender(this, obj);
	}

	public int getFPS() {
		return thread.getFPS();
	}

	public float getSPF() {
		return 1.0f / getFPS();
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setBackground(Color bg) {
		panel.setBackground(bg);
		win.setBackground(Color.BLACK);
	}

	public Color getBackground() {
		return panel.getBackground();
	}

	public Window() {
		this("LGGL Game");
	}

	public Window(String title) {
		init();
		setTitle(title);
		setSize(width, height);
	}

	public ViewportManager getViewportManager() {
		return viewport;
	}

	public void setViewportManager(ViewportManager manager) {
		viewport = manager;
	}

	public void setIcon(Image img) {
		win.setIconImage(img);
	}

	public WindowEventThread getEventThread() {
		return thread;
	}

	public Image getIcon() {
		return win.getIconImage();
	}

	/**
	 * Set whether or not if the Window is visible. If visible equals to true, it's
	 * will execute <code>show()</code>. If visible equals to false, it's will
	 * execute <code>hide()</code>.
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		if (visible == true) {
			show();
		}
		if (visible == false) {
			hide();
		}
	}

	/**
	 * Will execute <code>setVisible(true)</code> if visible is equals ignore case
	 * to "Visible" or "True" Or, it's will execute <code>setVisible(false)</code>
	 * if visible is equals ignore case to "Invisible" or "False" If visible is
	 * equals to nothing of these, it will execute <code>setVisible(false)</code>.
	 * 
	 * @param visible
	 */
	public void setVisible(String visible) {
		if (visible.equalsIgnoreCase("visible")) {
			setVisible(true);
		} else if (visible.equalsIgnoreCase("invisible")) {
			setVisible(false);
		} else {
			setVisible(Boolean.valueOf(visible));
		}
	}

	public Window(String title, int width, int height) {
		init();
		setTitle(title);
		setSize(width, height);
	}

	public Window(int width, int height) {
		this("LGGL Game", width, height);
	}

	private void init() {
		if (render == null)
			setRenderer(new Lightning());
		win.setLayout(null);
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		win.add(panel);
		win.getContentPane().setBackground(Color.BLACK);
		win.addKeyListener(input);
		panel.addMouseMotionListener((MouseMotionListener) mouse);
		panel.addMouseListener((MouseListener) mouse);
		panel.addMouseWheelListener((MouseWheelListener) mouse);
		win.setResizable(false);
		win.addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				hide();
				win.dispose();
			}
		});
		thread.start();
		objectContainer = new Container();
		camera = new Camera();
	}

	public void setViewport(int x, int y, int width, int height) {
		panel.setLocation(x, y);
		panel.setSize(width, height);
		objectContainer.setSize(width, height);
		vW = width;
		vH = height;
	}

	public Rectangle getViewport() {
		return new Rectangle(panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight());
	}

	public JFrame getJFrame() {
		return win;
	}

	private GraphicsDevice device;

	private int fullscreenWidth, fullscreenHeight;

	/**
	 * 
	 * @return requested fullscreen width
	 */
	public int getFullscreenWidth() {
		return fullscreenWidth;
	}

	/**
	 * It is the wanted fullscreen width, LGGL will try to fit the window to that
	 * width a maximum
	 * 
	 * @param fullscreenWidth
	 */
	public void setFullscreenWidth(int fullscreenWidth) {
		this.fullscreenWidth = fullscreenWidth;
	}

	public int getFullscreenHeight() {
		return fullscreenHeight;
	}

	/**
	 * It is the wanted fullscreen height, LGGL will try to fit the window to that
	 * height a maximum
	 * 
	 * @param fullscreenWidth
	 */
	public void setFullscreenHeight(int fullscreenHeight) {
		this.fullscreenHeight = fullscreenHeight;
	}

	public void setFullscreen(boolean fullscreen) {
		try {
			if (fullscreen == true) {
				if (device.isFullScreenSupported() && !legacyFullscreen) {
					device.setFullScreenWindow(win);
					DisplayMode found = device.getDisplayMode();
					if (fullscreenWidth != 0 && fullscreenHeight != 0) {
						for (DisplayMode mode : device.getDisplayModes()) {
							if ((mode.getWidth() >= fullscreenWidth && mode.getWidth() < found.getWidth())) {
								if (mode.getHeight() >= fullscreenHeight && mode.getHeight() < found.getHeight()) {
									found = mode;
								}
							}
						}
					}
					if (!found.equals(device.getDisplayMode())) {
						device.setDisplayMode(found);
					}

					win.createBufferStrategy(1);

				} else {
					win.dispose();

					win.setUndecorated(true);
					win.setExtendedState(JFrame.MAXIMIZED_BOTH);

					show();
				}
			} else {
				if (device.isFullScreenSupported() && !legacyFullscreen) {
					device.setFullScreenWindow(null);
					fullscreen = false;
				} else {
					win.dispose();

					win.setUndecorated(false);
					win.setExtendedState(JFrame.NORMAL);
					setSize(win.getWidth(), win.getHeight());

					show();
				}
			}
			this.fullscreen = fullscreen;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setResizable(boolean resize) {
		win.setResizable(resize);
	}

	public void setSize(int w, int h) {
		width = w;
		height = h;
		win.setSize(w, h);
	}

	public void show() {
		// showinit();
		win.setVisible(true);
	}

	public void hide() {
		win.setVisible(false);
	}

	public void setTitle(String newTitle) {
		title = newTitle;
		win.setTitle(title);
	}

	public Keyboard getKeyboard() {
		return input;
	}

	public boolean isClosed() {
		return !win.isVisible();
	}

	public boolean isVisible() {
		return win.isVisible();
	}

	public void add(GameObject obj) {
		objectContainer.add(obj);
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public void setCamera(Camera cam) {
		camera = cam;
	}

	public void update() {
		if (viewport != null) {
			Rectangle view = viewport.getViewport(this);
			if (!view.equals(panel.getBounds())) {
				setViewport(view.x, view.y, view.width, view.height);
			}
		}
		if (customGraphics == null) {
			if (!isFullscreen()) {
				win.setIgnoreRepaint(false);
				panel.setIgnoreRepaint(false);
				panel.repaint();
			} else {
				win.setIgnoreRepaint(true);
				panel.setIgnoreRepaint(true);
				BufferStrategy bs = win.getBufferStrategy();
				Graphics g = bs.getDrawGraphics();
				win.paint(g);
				bs.show();
			}
		} else {
			// Custom double-buffering
			int w = vW;
			int h = vH;
			if (w < 1)
				w = 1;
			if (h < 1)
				h = 1;
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
			panel.paint(img.createGraphics());
			System.out.println(img);
			customGraphics.drawImage(img, panel.getX(), panel.getY(), null);
		}
	}

	public GameObject[] getObjects() {
		return objectContainer.getObjects();
	}

	public void remove(GameObject obj) {
		try {
			objectContainer.remove(obj);
		} catch (Exception e) {
			throw e;
		}
	}

	public int getWidth() {
		return win.getWidth();
	}

	public int getHeight() {
		return win.getHeight();
	}

	public Mouse getMouse() {
		return mouse;
	}

	public void fireEvent(String type, Object... args) {
		if (type.equals("mousePressed")) {
			GameObject[] a = getObjects();
			int mx = (int) args[0];
			int my = (int) args[1];
			focusedObj = null;
			for (GameObject b : a) {

				if (mx > b.getX() && my > b.getY() && mx < b.getX() + b.getWidth() && my < b.getY() + b.getHeight()) {
					focusedObj = b;
					break;
				}
			}
		}
		if (focusedObj != null)
			focusedObj.onEvent(type, args);
	}

	public void removeAll() {
		for (GameObject obj : objects) {
			remove(obj);
		}
	}
}
