package electory.client;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.ARBDebugOutputCallback;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

import electory.client.audio.SoundManager;
import electory.client.gui.FontRenderer;
import electory.client.gui.GuiRenderState;
import electory.client.gui.ResolutionScaler;
import electory.client.gui.screen.GuiInGame;
import electory.client.gui.screen.GuiPause;
import electory.client.gui.screen.GuiScreen;
import electory.client.render.AtlasManager;
import electory.client.render.shader.ShaderCompileException;
import electory.client.render.shader.ShaderManager;
import electory.client.render.texture.TextureManager;
import electory.client.render.world.WorldRenderer;
import electory.entity.EntityPlayer;
import electory.nbt.ShortArrayTag;
import electory.utils.CrashException;
import electory.utils.TickTimer;
import electory.world.World;

public class TinyCraft {
	private static TinyCraft instance = new TinyCraft();

	public World world = new World();
	public EntityPlayer player = null;// new EntityPlayer(world);
	public TextureManager textureManager = new TextureManager();
	public TickTimer tickTimer = new TickTimer(20.0f);
	public ResolutionScaler resolutionScaler = new ResolutionScaler(1.0f);
	public GuiInGame theHUD = new GuiInGame(this);
	private DisplayMode windowedDisplayMode = new DisplayMode(800, 500);
	public WorldRenderer worldRenderer = new WorldRenderer(world);
	private GuiRenderState renderState = new GuiRenderState();
	public FontRenderer fontRenderer = new FontRenderer();
	public SoundManager soundManager = new SoundManager();
	public GuiScreen currentGui = null;
	// public ChunkLoadThread chunkLoadThread = new ChunkLoadThread();
	private int width = 0, height = 0;

	public int fps = 0;
	private int fpsc = 0;
	public int chunkUpdCounter = 0;
	public int chunkUpdates = 0;
	private long fpsNanoCounterLast = System.nanoTime();
	private long fpsNanoCounter = System.nanoTime();
	
	private static String version = "version unknown";

	private boolean shutdown = false;

	static {
		ShortArrayTag.register();
	}

	public static void main(String[] args) {
		getInstance().start();
	}

	public static TinyCraft getInstance() {
		return instance;
	}

	public TinyCraft() {
	}

	public boolean isPaused() {
		return currentGui == null ? false : currentGui.doesGuiPauseGame();
	}

	public void shutdown() {
		shutdown = true;
	}
	
	static {
		BufferedReader isr = new BufferedReader(new InputStreamReader(TinyCraft.class.getResourceAsStream("/version.def")));
		try {
			version = isr.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getVersion() {
		return version;
	}

	public void start() {
		initRenderer();
		initGame();

		while (!Display.isCloseRequested() && !shutdown) {
			update();
			// if (Display.isActive() || Display.isDirty()) {
			render();
			// }
			Display.update();
			fpsc++;
			fpsNanoCounter = System.nanoTime();
			if (fpsNanoCounter >= fpsNanoCounterLast + 1000000000L) {
				fpsNanoCounterLast += 1000000000L;
				// Display.setTitle(fps + " FPS");
				fps = fpsc;
				chunkUpdates = chunkUpdCounter;
				chunkUpdCounter = 0;
				fpsc = 0;
			}
		}

		soundManager.destroy();

		world.unload();

		Display.destroy();
	}

	public void initRenderer() {
		try {
			Display.setDisplayMode(windowedDisplayMode);
			Display.setResizable(true);
			Display.setTitle("Electory");
			PixelFormat pf = new PixelFormat();
			ContextAttribs attribs = new ContextAttribs(2, 1);
			Display.setIcon(loadIcon("/img/icon.png"));
			Display.create(pf, attribs);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		try {
			Keyboard.create();
			Mouse.create();
			Mouse.setGrabbed(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		System.out.println("Max color attachments: "
				+ GL11.glGetInteger(EXTFramebufferObject.GL_MAX_COLOR_ATTACHMENTS_EXT));
		System.out.println("GL vendor: " + GL11.glGetString(GL11.GL_VENDOR));
		System.out.println("GL version: " + GL11.glGetString(GL11.GL_VERSION));
		System.out.println("GL renderer: " + GL11.glGetString(GL11.GL_RENDERER));
		System.out.println("GLSL version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		System.out.println("GL extensions: " + GL11.glGetString(GL11.GL_EXTENSIONS));
		System.out.println("Max texture size: " + GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE));

		if (GLContext.getCapabilities().GL_ARB_debug_output) {
			ARBDebugOutput.glDebugMessageCallbackARB(new ARBDebugOutputCallback(new ARBDebugOutputCallback.Handler() {
				@Override
				public void handleMessage(int source, int type, int id, int severity, String message) {
					System.out.println("OpenGL message, source "
							+ source
							+ " type "
							+ type
							+ " id "
							+ id
							+ " severity "
							+ severity
							+ ": "
							+ message);
				}
			}));
		}

		try {
			ShaderManager.init();
		} catch (IOException | ShaderCompileException e) {
			throw new CrashException(e);
		}

		soundManager.init();
		// ShaderManager.defaultProgram.use();
		AtlasManager.registerAllTerrainSprites();
	}

	public void initGame() {
		try {
			world.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		if (!Display.isActive() && world != null && player != null) {
			openGui(new GuiPause(this));
		}

		tickTimer.updateTimer();

		if (player != null) {
			soundManager.updateListener(player);
		}

		for (int i = 0; i < tickTimer.elapsedTicks; i++) {
			tick(tickTimer.renderPartialTicks);
		}

		while (Mouse.next()) {
			if (player != null && currentGui == null && !isPaused()) {
				player.playerController.processMouseEvent(player);
			}
			if (currentGui != null) {
				currentGui.handleMouseEvent(MouseEvent.fromLWJGLEvent().adjustToGuiScale(resolutionScaler));
			}
		}

		while (Keyboard.next()) {
			if (currentGui == null) {
				if (player != null && !isPaused()) {
					theHUD.handleKeyEvent(Keyboard.getEventKey(), Keyboard.getEventKeyState());
				}
			} else {
				currentGui.handleKeyEvent(Keyboard.getEventKey(), Keyboard.getEventKeyState());
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_F11 && Keyboard.getEventKeyState()) {
				if (Display.isFullscreen()) {
					try {
						Display.setFullscreen(false);
						Display.setDisplayMode(windowedDisplayMode);
						Mouse.setGrabbed(false);
						Mouse.destroy();
						Mouse.create();
						// Mouse.setGrabbed(true);
					} catch (LWJGLException e) {
						e.printStackTrace();
					}
				} else {
					try {
						windowedDisplayMode = Display.getDisplayMode();
						Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
						Mouse.setGrabbed(false);
						Mouse.destroy();
						Mouse.create();
						// Mouse.setGrabbed(true);
					} catch (LWJGLException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public File getGameDir() {
		File dir = new File(System.getProperty("user.dir"));
		return dir;
	}

	public File getUserDataDir() {
		File dir = new File(getGameDir(), "userdata");
		dir.mkdirs();
		return dir;
	}

	public static long getSystemTime() {
		return Sys.getTime() * 1000L / Sys.getTimerResolution();
	}

	public void render() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());

		GL11.glClearColor(0.52f, 0.8f, 0.92f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		if (width != Display.getWidth() || height != Display.getHeight()) {
			width = Display.getWidth();
			height = Display.getHeight();
			worldRenderer.updateScreenSize();
			if (currentGui != null) {
				currentGui.setupGuiElementsForScreenSize(resolutionScaler);
			}
		}

		resolutionScaler.setupOrtho(renderState);

		if (player != null) {
			worldRenderer.render(tickTimer.renderPartialTicks);

			theHUD.renderGui(renderState);
		}

		if (currentGui != null) {
			currentGui.renderGui(renderState);
			Mouse.setGrabbed(false);
		} else {
			Mouse.setGrabbed(true);
		}
	}

	public void tick(float renderPartialTicks) {
		if (!isPaused()) {
			world.update();
		}
	}

	public static ByteBuffer[] loadIcon(String filepath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(TinyCraft.class.getResource(filepath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteBuffer[] buffers = new ByteBuffer[3];
		buffers[0] = loadIconInstance(image, 128);
		buffers[1] = loadIconInstance(image, 32);
		buffers[2] = loadIconInstance(image, 16);
		return buffers;
	}

	private static ByteBuffer loadIconInstance(BufferedImage image, int dimension) {
		BufferedImage scaledIcon = new BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaledIcon.createGraphics();
		double ratio = 1;
		if (image.getWidth() > scaledIcon.getWidth()) {
			ratio = (double) (scaledIcon.getWidth()) / image.getWidth();
		} else {
			ratio = (int) (scaledIcon.getWidth() / image.getWidth());
		}
		if (image.getHeight() > scaledIcon.getHeight()) {
			double r2 = (double) (scaledIcon.getHeight()) / image.getHeight();
			if (r2 < ratio) {
				ratio = r2;
			}
		} else {
			double r2 = (int) (scaledIcon.getHeight() / image.getHeight());
			if (r2 < ratio) {
				ratio = r2;
			}
		}
		double width = image.getWidth() * ratio;
		double height = image.getHeight() * ratio;
		g.drawImage(image,
					(int) ((scaledIcon.getWidth() - width) / 2),
					(int) ((scaledIcon.getHeight() - height) / 2),
					(int) (width),
					(int) (height),
					null);
		g.dispose();

		byte[] imageBuffer = new byte[dimension * dimension * 4];
		int counter = 0;
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				int colorSpace = scaledIcon.getRGB(j, i);
				imageBuffer[counter + 0] = (byte) ((colorSpace << 8) >> 24);
				imageBuffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
				imageBuffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
				imageBuffer[counter + 3] = (byte) (colorSpace >> 24);
				counter += 4;
			}
		}
		return ByteBuffer.wrap(imageBuffer);
	}

	public void setPlayer(EntityPlayer player) {
		if (this.player != null) {
			this.player.playerController = null;
		}
		this.player = player;
		this.player.playerController = new PlayerControllerClient();
	}

	public void openGui(GuiScreen gui) {
		currentGui = gui;
		if (gui != null) {
			gui.setupGuiElementsForScreenSize(resolutionScaler);
		}
	}
}
