package test.org.powerhigh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.powerhigh.Material;
import org.powerhigh.SizedViewport;
import org.powerhigh.audio.Audio;
import org.powerhigh.cpak.CPakWriter;
import org.powerhigh.game.SimpleGame;
import org.powerhigh.graphics.Animation;
import org.powerhigh.graphics.Drawer;
import org.powerhigh.graphics.Interface;
import org.powerhigh.graphics.ParticleBlueprint;
import org.powerhigh.graphics.ParticleBlueprint.ParticleRenderer;
import org.powerhigh.graphics.ParticleBox;
import org.powerhigh.graphics.renderers.SimpleRenderer;
import org.powerhigh.graphics.renderers.lightning.Lightning;
import org.powerhigh.input.AbstractKeyboard;
import org.powerhigh.input.KeyCodes;
import org.powerhigh.input.Mouse;
import org.powerhigh.multiplayer.Connection;
import org.powerhigh.multiplayer.Server;
import org.powerhigh.objects.Button;
import org.powerhigh.objects.Particle;
import org.powerhigh.objects.Rectangle;
import org.powerhigh.objects.Sprite;
import org.powerhigh.objects.Text;
import org.powerhigh.utils.Color;
import org.powerhigh.utils.PowerHighException;
import org.powerhigh.utils.debug.DebugLogger;

public class PowerHighTest extends SimpleGame {

	private Sprite player;
	private Text fps;
	private ParticleBox box;
	private ParticleBlueprint damageBlueprint;
	private boolean scaleUp;
	private ImplementationSettings impl = new ImplementationSettings(ImplementationSettings.Interface.SWING, ImplementationSettings.Audio.AWT);

	@Override
	public void update(Interface win, double delta) {
		handleKeys(win, delta);
//		throw new Error("Random error in random game.");
	}
	
	public void exit(Interface win) {
		System.exit(0);
	}

	public void handleKeys(Interface win, double delta) {
		AbstractKeyboard keyboard = win.getInput().getKeyboard();
		int speed = (int) (5d * delta);
		player.setX((int) (player.getX() + (input.getAxis("Horizental") * speed)));
		player.setY((int) (player.getY() + (input.getAxis("Vertical") * speed)));
		if (keyboard.isKeyDown(KeyCodes.KEY_G)) {
			if (!(Interface.getRenderer() instanceof SimpleRenderer)) {
				DebugLogger.logInfo("Changed Renderer to: Simple (Fast, Unaccurate)");
				Interface.setRenderer(new SimpleRenderer());
			}
		}
		if (keyboard.isKeyDown(KeyCodes.KEY_H)) {
			if (!(Interface.getRenderer() instanceof Lightning)) {
				DebugLogger.logInfo("Changed Renderer to: Lightning (Default, Accurate)");
				Interface.setRenderer(new Lightning());
			}
		}
//		if (keyboard.isKeyDown(KeyCodes.KEY_ESCAPE)) {
//			Mouse.setGrabbed(false);
//			Mouse.setCursorHidden(false);
//		}
//		if (keyboard.isKeyDown(KeyCodes.KEY_E)) {
//			Mouse.setGrabbed(true);
//			Mouse.setCursorHidden(true);
//		}
//		
		Particle p = new Particle(damageBlueprint, Mouse.getX(), Mouse.getY());
		box.addParticle(p);
		//System.out.println(delta);
		if (!Double.isFinite(delta)) {
			delta = 1d;
		}
		box.windRight((int)(10d*delta), (int)(5*delta));
		fps.setText("FPS: " + win.getFPS() + ", SPF: " + win.getSPF() + ", Delta: " + win.getEventThread().getDelta());
		player.setRotation(player.getRotation() + 1);
		win.getCamera().setXOffset(win.getCamera().getXOffset() + Mouse.getDX());
		win.getCamera().setYOffset(win.getCamera().getYOffset() + Mouse.getDY());
		Mouse.clearMouseVelocity();
//		if (scaleUp) {
//			win.getCamera().setScale(win.getCamera().getScale() + 0.01d);
//		} else {
//			win.getCamera().setScale(win.getCamera().getScale() - 0.01d);
//		}
//		if (win.getCamera().getScale() > 3) {
//			scaleUp = false;
//		}
//		if (win.getCamera().getScale() < 0.5d) {
//			scaleUp = true;
//		}
//		win.getCamera().setRotation(win.getCamera().getRotation() + 1);
//		if (win.getCamera().getRotation() > 360) {
//			win.getCamera().setRotation(0);
//		}
	}
	
	public void dbgsound() {
//		WavMusic music = null;
//		try {
//			//System.out.println(Audio.AUDIO_BIT_16 | Audio.DVD_SPEED);
//			music = new WavMusic(Audio.AUDIO_BIT_16 | Audio.CD_SPEED, 1.0f, new File("Alonzo - Santana.wav"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		audio.playMusic(music);
		try {
			CPakWriter writer = new CPakWriter(new FileOutputStream("dumbpack.cpak"), CPakWriter.GZIP_COMPRESSION);
			HashMap<String, Object> entries = new HashMap<>();
			entries.put("dummyId", 156145);
			entries.put("dummyName", "Dummy pack");
			entries.put("dummyData", "DDDDDDDAAAAAAAAAAAAAAATTTTTAAAAAAAA");
			entries.put("dummyTexture", player.getAnimation().getCurrentSprite());
			writer.writeCPakEntries(entries);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] fromu16(int x) {
		byte b2 = (byte) ((x % 256));
		x = (x - x % 256)/256;
		byte b1 = (byte) ((x % 256));
		return new byte[] {b1, b2};
	}
	
	public void server() {
		Server server = Server.connect(new BasicServerHandler(), 4192);
		Connection con = Connection.connect("127.0.0.1", 4192);
		try {
			con.sendStablePacket(new byte[] {1, 1});  // TCP
			con.sendPacket(new byte[] {1, 1});        // UDP
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			con.close();
			//server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Interface win) {
		try {
			audio = new Audio(Audio.AUDIO_BIT_16);
		} catch (PowerHighException e) {
			e.printStackTrace();
		}
		Rectangle rect = new Rectangle(100, 100, 100, 100, Color.YELLOW);
		win.add(rect);
		win.setViewportManager(new SizedViewport(800, 600));
		//server();
		player = new Sprite();
		fps = new Text();
		box = new ParticleBox(512);
		box.setX(0);
		box.setY(0);
		box.setSize(1920, 1080);
		ParticleRenderer damageRenderer = new ParticleRenderer() {

			@Override
			public void render(Drawer d, Particle p) {
				int a = p.getLife() % 255;
				if (a < 1) {
					a = 1;
				}
				a -= 255;
				a *= -1;
				Color c = new Color(255, 255, 255, a);
				d.setColor(c);
				int size = p.getBlueprint().getSize();
//				if (a != 0) {
//					int d = a / 100;
//					if (d<1)d=1;
//					size /= d;
//				}
				d.fillRect(p.getX(), p.getY(), size, size);
			}
			
		};
		damageBlueprint = new ParticleBlueprint(10, (short)255, damageRenderer);
		player.setSize(128, 128);
		player.setColor(Color.YELLOW);
		player.setMaterial(new Material(0.2f));
		//player.centerTo(win);
		try {
			player.setAnimation(new Animation(new File("Dumb Man.gan")));
			player.getAnimation().start();
		} catch (IOException e2) {
			e2.printStackTrace();
			player.setSize(128, 128);
		}
		win.setBackground(Color.CYAN);
		win.add(player);
		
		Button bt = new Button();
		bt.setText("Musique !");
		bt.setX(100);
		bt.setY(200);
		fps.setY(24);
		fps.setColor(Color.WHITE);
		bt.setWidth(100);
		bt.setHeight(60);
		bt.addAction(new Runnable() {

			@Override
			public void run() {
				if (bt.getText().equals("Hello World!")) {
					bt.setSize(200, 80);
					bt.setText("Test, again");
				} else {
					bt.setSize(100, 60);
					bt.setText("Hello World!");
				}
				dbgsound();
			}

		});
		Button stj = new Button("Switch to JavaFX");
		stj.setX(400);
		stj.setY(100);
		stj.setSize(100, 60);
		stj.addAction(() -> {
			impl = new ImplementationSettings(ImplementationSettings.Interface.JAVAFX, ImplementationSettings.Audio.AWT);
			restartImplementation();
		});
		
		Button sts = new Button("Switch to Swing");
		sts.setX(550);
		sts.setY(100);
		sts.setSize(100, 60);
		sts.addAction(() -> {
			impl = new ImplementationSettings(ImplementationSettings.Interface.SWING, ImplementationSettings.Audio.AWT);
			restartImplementation();
		});
		
		win.add(bt);
		win.add(stj);
		win.add(sts);
		win.add(fps);

		win.add(box);
		
		setFrameRate(60);
	}

	public static void main(String[] args) {
		PowerHighTest test = new PowerHighTest();
		test.start();
	}

	@Override
	public ImplementationSettings getImplementationSettings() {
		return impl;
	}

}