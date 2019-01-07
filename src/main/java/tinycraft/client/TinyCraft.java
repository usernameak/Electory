package tinycraft.client;

import static tinycraft.math.MathUtils.deg2rad;

import java.io.File;
import java.io.IOException;

import org.joml.AxisAngle4f;
import org.joml.Vector3f;
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

import tinycraft.block.Block;
import tinycraft.client.gui.FontRenderer;
import tinycraft.client.gui.GuiInGame;
import tinycraft.client.gui.GuiRenderState;
import tinycraft.client.gui.ResolutionScaler;
import tinycraft.client.render.AtlasManager;
import tinycraft.client.render.shader.ShaderCompileException;
import tinycraft.client.render.shader.ShaderManager;
import tinycraft.client.render.texture.TextureManager;
import tinycraft.client.render.world.WorldRenderer;
import tinycraft.entity.EntityPlayer;
import tinycraft.math.Ray;
import tinycraft.math.Ray.AABBIntersectionResult;
import tinycraft.utils.CrashException;
import tinycraft.utils.EnumSide;
import tinycraft.utils.TickTimer;
import tinycraft.world.World;

public class TinyCraft {
	private static TinyCraft instance = new TinyCraft();

	public World world = new World();
	public EntityPlayer player = null;// new EntityPlayer(world);
	public TextureManager textureManager = new TextureManager();
	public TickTimer tickTimer = new TickTimer(20.0f);
	public ResolutionScaler resolutionScaler = new ResolutionScaler(2.0f);
	public GuiInGame theHUD = new GuiInGame(this);
	private DisplayMode windowedDisplayMode = new DisplayMode(800, 500);
	public WorldRenderer worldRenderer = new WorldRenderer(world);
	private GuiRenderState renderState = new GuiRenderState();
	public FontRenderer fontRenderer = new FontRenderer();
	// public ChunkLoadThread chunkLoadThread = new ChunkLoadThread();
	private int width = 0, height = 0;

	public int fps = 0;
	private int fpsc = 0;
	private long fpsNanoCounterLast = System.nanoTime();
	private long fpsNanoCounter = System.nanoTime();

	public static void main(String[] args) {
		getInstance().start();
	}

	public static TinyCraft getInstance() {
		return instance;
	}

	public TinyCraft() {
	}

	public void start() {
		initRenderer();
		initGame();

		while (!Display.isCloseRequested()) {
			update();
			if (Display.isActive() || Display.isDirty()) {
				render();
				if(!Display.isActive()) {
					Display.update();
					render();
				}
			}
			Display.update();
			fpsc++;
			fpsNanoCounter = System.nanoTime();
			if (fpsNanoCounter >= fpsNanoCounterLast + 1000000000L) {
				fpsNanoCounterLast += 1000000000L;
				// Display.setTitle(fps + " FPS");
				fps = fpsc;
				fpsc = 0;
			}
		}
	}

	public void initRenderer() {
		try {
			Display.setDisplayMode(windowedDisplayMode);
			Display.setResizable(true);
			Display.setTitle("Electory");
			PixelFormat pf = new PixelFormat();
			ContextAttribs attribs = new ContextAttribs(2, 1);
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

		if (GLContext.getCapabilities().GL_ARB_debug_output) {
			ARBDebugOutput.glDebugMessageCallbackARB(new ARBDebugOutputCallback(new ARBDebugOutputCallback.Handler() {
				@Override
				public void handleMessage(int source, int type, int id, int severity, String message) {
					System.out.println("OpenGL message, source " + source + " type " + type + " id " + id + " severity "
							+ severity + ": " + message);
				}
			}));
		}

		try {
			ShaderManager.init();
		} catch (IOException | ShaderCompileException e) {
			throw new CrashException(e);
		}
		// ShaderManager.defaultProgram.use();
		AtlasManager.registerAllTerrainSprites();
	}

	public void initGame() {
		/*
		 * player.setPosition(0, 128, 0, false); player.yaw = 0.0f; player.pitch = 0.0f;
		 */
		// chunkLoadThread.start();
	}

	public void update() {
		tickTimer.updateTimer();

		for (int i = 0; i < tickTimer.elapsedTicks; i++) {
			tick(tickTimer.renderPartialTicks);
		}

		while (Mouse.next()) {
			if (player != null) {
				player.yaw += Mouse.getEventDX() * 0.1f;
				player.pitch += Mouse.getEventDY() * 0.1f;
				if (player.pitch > 90f) {
					player.pitch = 90f;
				} else if (player.pitch < -90f) {
					player.pitch = -90f;
				}
				if (Mouse.getEventButton() == 0 || Mouse.getEventButton() == 1) {
					if (Mouse.getEventButtonState()) {
						Vector3f pos = player.getInterpolatedPosition(tickTimer.renderPartialTicks);
						pos.add(0f, player.getEyeHeight(), 0f);

						Ray ray = new Ray(new Vector3f(pos.x, pos.y, pos.z), new Vector3f(0f, 0f, 1f))
								.rotate(new AxisAngle4f((float) deg2rad(-player.pitch), 1f, 0f, 0f))
								.rotate(new AxisAngle4f((float) deg2rad(-player.yaw), 0f, 1f, 0f));

						int x1 = (int) Math.floor(pos.x - 5);
						int y1 = (int) Math.floor(pos.y - 5);
						int z1 = (int) Math.floor(pos.z - 5);
						int x2 = (int) Math.ceil(pos.x + 5);
						int y2 = (int) Math.ceil(pos.y + 5);
						int z2 = (int) Math.ceil(pos.z + 5);

						int tx = 0, ty = 0, tz = 0;
						AABBIntersectionResult tres = new AABBIntersectionResult(false, Float.MAX_VALUE,
								EnumSide.UNKNOWN);

						for (int x = x1; x <= x2; x++) {
							for (int y = y1; y <= y2; y++) {
								for (int z = z1; z <= z2; z++) {
									Block block = world.getBlockAt(x, y, z);
									if (block != null && !block.isLiquid()) {
										AABBIntersectionResult res = ray
												.intersectsAABB(block.getAABB(world, x, y, z, false));
										if (res.hasHit && res.distance < tres.distance) {
											tres = res;
											tx = x;
											ty = y;
											tz = z;
										}
									}
								}
							}
						}

						if (tres.hasHit && tres.distance <= 5.0f) {
							if (Mouse.getEventButton() == 0 && world.getBlockAt(tx, ty, tz).isBreakable()) {
								world.breakBlockWithParticles(tx, ty, tz);
							} else if (Mouse.getEventButton() == 1) {
								world.interactWithBlock(player, tx, ty, tz, tres.side);
							}
						}
					}
				}
			}
		}

		while (Keyboard.next()) {
			theHUD.handleKeyEvent(Keyboard.getEventKey(), Keyboard.getEventKeyState());
			if (Keyboard.getEventKey() == Keyboard.KEY_F11 && Keyboard.getEventKeyState()) {
				if (Display.isFullscreen()) {
					try {
						Display.setFullscreen(false);
						Display.setDisplayMode(windowedDisplayMode);
						Mouse.setGrabbed(false);
						Mouse.destroy();
						Mouse.create();
						Mouse.setGrabbed(true);
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
						Mouse.setGrabbed(true);
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
		}

		if (player != null) {
			worldRenderer.render(tickTimer.renderPartialTicks);

			resolutionScaler.setupOrtho(renderState);

			theHUD.renderGui(renderState);
		}
	}

	public void tick(float renderPartialTicks) {
		world.update();
	}
}
