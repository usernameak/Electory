package electory.client;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import electory.block.Block;
import electory.client.audio.AudioSource;
import electory.client.audio.SoundManager;
import electory.client.console.Console;
import electory.client.event.KeyEvent;
import electory.client.gui.FontRenderer;
import electory.client.gui.GuiRenderState;
import electory.client.gui.ResolutionScaler;
import electory.client.gui.screen.GuiConsole;
import electory.client.gui.screen.GuiInGame;
import electory.client.gui.screen.GuiMainMenu;
import electory.client.gui.screen.GuiPause;
import electory.client.gui.screen.GuiScreen;
import electory.client.render.AtlasManager;
import electory.client.render.shader.ShaderCompileException;
import electory.client.render.shader.ShaderManager;
import electory.client.render.texture.TextureManager;
import electory.client.render.world.WorldRenderer;
import electory.entity.EntityPlayer;
import electory.event.ElectoryInitEvent;
import electory.event.EventRegistry;
import electory.event.EventType;
import electory.event.HandleEvent;
import electory.event.RegisterBlocksEvent;
import electory.nbt.ShortArrayTag;
import electory.scripting.ScriptingEngine;
import electory.utils.CrashException;
import electory.utils.TickTimer;
import electory.world.World;

public class TinyCraft {
	private static TinyCraft instance;

	public World world;// = new WorldSP();
	public EntityPlayer player = null;// new EntityPlayer(world);
	public TextureManager textureManager = new TextureManager();
	public TickTimer tickTimer = new TickTimer(20.0f);
	public ResolutionScaler resolutionScaler = new ResolutionScaler(1.0f);
	public GuiInGame theHUD = new GuiInGame(this);
	// private DisplayMode windowedDisplayMode = new DisplayMode(800, 500);
	public WorldRenderer worldRenderer = new WorldRenderer();
	private GuiRenderState renderState = new GuiRenderState();
	public FontRenderer fontRenderer = new FontRenderer();
	public SoundManager soundManager = new SoundManager();
	public GuiScreen currentGui;
	public Console console = null;
	public ScriptingEngine scriptingEngine;
	public Logger logger;
	public boolean hadWorld = false;
	// public ChunkLoadThread chunkLoadThread = new ChunkLoadThread();
	private int width = 0, height = 0;
	private double oldCursorPosX = 0;
	private double oldCursorPosY = 0;

	public int fps = 0;
	private int fpsc = 0;
	public volatile int chunkUpdCounter = 0; // TODO: unsafe
	public int chunkUpdates = 0;
	private long fpsNanoCounterLast = System.nanoTime();
	private long fpsNanoCounter = System.nanoTime();

	public long window;

	private static String version = "version unknown";

	private boolean shutdown = false;

	static {
		ShortArrayTag.register();
	}

	public static void main(String[] args) {
		getInstance().start();
	}

	public static TinyCraft getInstance() {
		return instance == null ? (instance = new TinyCraft()) : instance;
	}

	public TinyCraft() {
	}

	public void setWorld(World world) {
		this.world = world;
		worldRenderer.setWorld(world);
	}

	public boolean isPaused() {
		return currentGui == null ? false : currentGui.doesGuiPauseGame();
	}

	public void shutdown() {
		shutdown = true;
	}

	static {
		BufferedReader isr = new BufferedReader(
				new InputStreamReader(TinyCraft.class.getResourceAsStream("/version.def")));
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
	
	public void initEvents() {
		eventRegistry.registerEventType(new EventType("init", ElectoryInitEvent.class));
		eventRegistry.registerEventType(new EventType("register_blocks", RegisterBlocksEvent.class));
		eventRegistry.registerEventType(new EventType("key_event", KeyEvent.class));
		
		eventRegistry.registerHandler(this);
	}

	public void start() {
		try {
			initLogging();
			initEvents();
			initRenderer();
			initGame();
			postInitRenderer();
			initConsole();

			while (!GLFW.glfwWindowShouldClose(window) && !shutdown) {
				update();
				soundManager.update();
				if (GLFW.GLFW_TRUE == GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED)) {
					render();
				}
				GLFW.glfwPollEvents();

				GLFW.glfwSwapBuffers(window);
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

			if (world != null) {
				world.unload();
			}

			GLFW.glfwTerminate();

			worldRenderer.terminate();
		} catch (CrashException e) {
			showCrashReport(e);
		} catch (Exception e) {
			showCrashReport(new CrashException(e));
		}
	}

	private void postInitRenderer() {
		AtlasManager.registerAllTerrainSprites();
	}

	private void initLogging() {
		InputStream stream = getClass().getResourceAsStream("/logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		this.logger = Logger.getLogger("Electory");
	}

	public void showCrashReport(CrashException exception) {
		logger.log(Level.SEVERE, "Crash report", exception);
		exception.printStackTrace();

		try {
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			// Mouse.setGrabbed(false);
			/*
			 * Mouse.destroy(); Keyboard.destroy(); Display.destroy();
			 */
			GLFW.glfwTerminate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos, true, "UTF-8");
			exception.printStackTrace(ps);
			String s = baos.toString("UTF-8");
			JFrame crashFrame = new JFrame("Electory crash");
			crashFrame.setIconImage(ImageIO.read(getClass().getResource("/img/icon.png")));
			JPanel contentPane = new JPanel();
			contentPane.setLayout(new BorderLayout());
			contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
			JTextPane textArea = new JTextPane();
			textArea.setText(s);
			textArea.setEditable(false);
			contentPane.add(textArea, BorderLayout.CENTER);
			crashFrame.setContentPane(contentPane);
			crashFrame.setLocation(100, 100);
			crashFrame.setSize(640, 480);
			crashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			crashFrame.setVisible(true);

			logger.info("Showing crash report window.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onMouseEvent(MouseEvent event) {

		if (player != null && currentGui == null && !isPaused()) {
			player.playerController.processMouseEvent(player, event);
			theHUD.handleMouseEvent(event.adjustToGuiScale(resolutionScaler));
		}
		if (currentGui != null) {
			currentGui.handleMouseEvent(event.adjustToGuiScale(resolutionScaler));
		}
	}

	@HandleEvent
	public void onKeyEvent(KeyEvent event) {
		if (currentGui == null) {
			if (player != null && !isPaused()) {
				theHUD.handleKeyEvent(event);
			}
		} else {
			currentGui.handleKeyEvent(event);
		}
		if (event.getKey() == GLFW.GLFW_KEY_F11 && event.isKeyState()) {
			if (GLFW.glfwGetWindowMonitor(window) != 0L) {
				GLFW.glfwSetWindowMonitor(window, 0L, 0, 0, 800, 500, 0);
			} else {
				GLFWVidMode mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
				GLFW.glfwSetWindowMonitor(	window,
											GLFW.glfwGetPrimaryMonitor(),
											0,
											0,
											mode.width(),
											mode.height(),
											mode.refreshRate());
			}
		} else if (event.getKey() == GLFW.GLFW_KEY_C
				&& GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F3) == GLFW.GLFW_PRESS
				&& event.isKeyState()) {
			throw new CrashException("Debug crash.");
		}
	}

	public void initRenderer() {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!GLFW.glfwInit()) {
			throw new RuntimeException("Failed to init glfw");
		}
		// try {
		// Display.setDisplayMode(windowedDisplayMode);
		// Display.setResizable(true);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_FALSE);
		this.window = GLFW.glfwCreateWindow(800, 500, "Electory", 0L, 0L);
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();

		GLFW.glfwSetKeyCallback(window, new GLFWKeyCallbackI() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				KeyEvent event = new KeyEvent(key, action != GLFW.GLFW_RELEASE);
				eventRegistry.emit(event);
			}
		});

		GLFW.glfwSetCursorPosCallback(window, new GLFWCursorPosCallbackI() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				onMouseEvent(MouseEvent.createPosEvent(xpos, ypos, xpos - oldCursorPosX, ypos - oldCursorPosY));

				oldCursorPosX = xpos;
				oldCursorPosY = ypos;
			}
		});

		GLFW.glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallbackI() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				onMouseEvent(MouseEvent.createButtonEvent(oldCursorPosX, oldCursorPosY, button, action, mods));
			}
		});
		// Display.setTitle("Electory");
		// PixelFormat pf = new PixelFormat();
		// ContextAttribs attribs = new ContextAttribs(3, 2).withDebug(false);
		// Display.setIcon(loadIcon("/img/icon.png"));
		// Display.create(pf, attribs);
		/*
		 * } catch (LWJGLException e) { e.printStackTrace(); }
		 */

		/*
		 * try { Keyboard.create(); Mouse.create(); // Mouse.setGrabbed(true); } catch
		 * (LWJGLException e) { e.printStackTrace(); }
		 */

		logger.info("Max color attachments: " + GL11.glGetInteger(EXTFramebufferObject.GL_MAX_COLOR_ATTACHMENTS_EXT));
		logger.info("GL vendor: " + GL11.glGetString(GL11.GL_VENDOR));
		logger.info("GL version: " + GL11.glGetString(GL11.GL_VERSION));
		logger.info("GL renderer: " + GL11.glGetString(GL11.GL_RENDERER));
		logger.info("GLSL version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		logger.info("GL extensions: " + GL11.glGetString(GL11.GL_EXTENSIONS));
		logger.info("Max texture size: " + GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE));
		/*
		 * if (GLContext.getCapabilities().GL_ARB_debug_output) {
		 * ARBDebugOutput.glDebugMessageCallbackARB(new ARBDebugOutputCallback(new
		 * ARBDebugOutputCallback.Handler() {
		 * 
		 * @Override public void handleMessage(int source, int type, int id, int
		 * severity, String message) { /*if(type == KHRDebug.GL_DEBUG_TYPE_POP_GROUP ||
		 * type == KHRDebug.GL_DEBUG_TYPE_PUSH_GROUP) { return; } new
		 * Exception("OpenGL message, source " + source + " type " + type + " id " + id
		 * + " severity " + severity + ": " + message).printStackTrace();
		 */
		/*
		 * } /*})); }
		 */

		try {
			ShaderManager.init();
		} catch (IOException | ShaderCompileException e) {
			throw new CrashException(e);
		}

		soundManager.init();
		// ShaderManager.defaultProgram.use();
	}

	public void initGame() {

		logger.info("Initializing script engine");
		scriptingEngine = new ScriptingEngine();
		try {
			scriptingEngine.runScript("/scripts/main.lua");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		eventRegistry.emit(new ElectoryInitEvent());
		Block.registerBlocks();
		openGui(new GuiMainMenu(this));
		/*
		 * try { world.load(); } catch (IOException e) { e.printStackTrace(); }
		 */
	}

	public void initConsole() {
		console = new Console(this, new GuiConsole(this));
		console.init(); // FIXME: Fix this to autoload commands if it's possible
	}

	private long lastMillis = System.currentTimeMillis();

	public EventRegistry eventRegistry = new EventRegistry();

	public void update() {
		if (hadWorld && world == null) {
			soundManager.stopMusic("ingame_music");
			hadWorld = false;
		} else if (!hadWorld && world != null) {
			soundManager.play(	"ingame_music",
								new AudioSource("mus/cassette_1_chord_1.xm").setAmbient(true).setStreaming(true));
			hadWorld = true;
		}

		if (GLFW.GLFW_FALSE == GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED)
				&& world != null
				&& player != null
				&& currentGui == null) {
			if (lastMillis + 500L < System.currentTimeMillis()) {
				openGui(new GuiPause(this));
			}
		} else {
			lastMillis = System.currentTimeMillis();
		}

		tickTimer.updateTimer();

		if (player != null) {
			soundManager.updateListener(player);
		}

		for (int i = 0; i < tickTimer.elapsedTicks; i++) {
			tick(tickTimer.renderPartialTicks);
		}
		/*
		 * while (Mouse.next()) {
		 * 
		 * }
		 */

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
		return 0L /* Sys.getTime() * 1000L / Sys.getTimerResolution() */;
	}

	public void render() {
		int[] width_ = new int[1];
		int[] height_ = new int[1];

		GLFW.glfwGetFramebufferSize(TinyCraft.getInstance().window, width_, height_);

		int nwidth = width_[0];
		int nheight = height_[0];

		//

		GL11.glViewport(0, 0, nwidth, nheight);

		if (player != null) {
			GL11.glClearColor(0.52f, 0.8f, 0.92f, 1.0f);
		} else {
			GL11.glClearColor(0f, 0f, 0f, 1.0f);
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		if (width != nwidth || height != nheight) {
			width = nwidth;
			height = nheight;
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
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			// Mouse.setGrabbed(false);
		} else {
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			// Mouse.setGrabbed(true);
		}
	}

	public void tick(float renderPartialTicks) {
		if (!isPaused() && world != null) {
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
		if (this.player != null) {
			this.player.playerController = new PlayerControllerClient();
		}
	}

	public void openGui(GuiScreen gui) {
		if (currentGui != null) {
			currentGui.closeGuiScreen();
		}
		currentGui = gui;
		if (gui != null) {
			gui.setupGuiElementsForScreenSize(resolutionScaler);
			gui.openGuiScreen();
		}
	}
}
