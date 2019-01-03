package tinycraft;

import org.joml.AABBf;
import org.joml.AxisAngle4f;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import tinycraft.block.Block;
import tinycraft.entity.Entity;
import tinycraft.entity.EntityPlayer;
import tinycraft.math.Ray;
import tinycraft.math.Ray.AABBIntersectionResult;
import tinycraft.render.ChunkRenderer;
import tinycraft.render.TextureManager;
import tinycraft.utils.TickTimer;
import tinycraft.world.Chunk;
import tinycraft.world.World;

public class TinyCraft {
	private static TinyCraft instance = new TinyCraft();

	public World world = new World();
	public EntityPlayer player = new EntityPlayer(world);
	public TextureManager textureManager = new TextureManager();
	public TickTimer tickTimer = new TickTimer();

	public static void main(String[] args) {
		getInstance().start();
	}

	public static TinyCraft getInstance() {
		return instance;
	}

	public TinyCraft() {
		world.addEntity(player);
	}

	public void start() {
		initRenderer();
		initGame();

		while (!Display.isCloseRequested()) {
			update();
			render();
			Display.update();
		}
	}

	public void initRenderer() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 500));
			Display.setResizable(true);
			Display.create();
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

		world.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(ChunkRenderer::init);
	}

	public void initGame() {
		player.x = 128.0f;
		player.y = 128.0f;
		player.z = 128.0f;
		player.yaw = 0.0f;
		player.pitch = 0.0f;
	}

	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	public void update() {
		while (Mouse.next()) {
			player.yaw += Mouse.getEventDX() * 0.1f;
			player.pitch += Mouse.getEventDY() * 0.1f;
			if (Mouse.getEventButton() == 0) {
				if (Mouse.getEventButtonState()) {
					Ray ray = new Ray(new Vector3f(player.x, player.y + 1.5f, player.z), new Vector3f(0f, 0f, 1f))
							.rotate(new AxisAngle4f((float) deg2rad(-player.pitch), 1f, 0f, 0f))
							.rotate(new AxisAngle4f((float) deg2rad(-player.yaw), 0f, 1f, 0f));

					// System.out.println("Hit dir x: " + ray.direction.x + " y: " + ray.direction.y + " z: " + ray.direction.z);

					int x1 = (int) (player.x - 5);
					int y1 = (int) (player.y - 5);
					int z1 = (int) (player.z - 5);
					int x2 = (int) (player.x + 6);
					int y2 = (int) (player.y + 6);
					int z2 = (int) (player.z + 6);

					int tx = 0, ty = 0, tz = 0;
					AABBIntersectionResult tres = new AABBIntersectionResult(false, Float.MAX_VALUE);

					for (int x = x1; x <= x2; x++) {
						for (int y = y1; y <= y2; y++) {
							for (int z = z1; z <= z2; z++) {
								Block block = world.getBlockAt(x, y, z);
								if (block != null) {
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
						world.setBlockAt(tx, ty, tz, null);
						//System.out.println("Hit x: " + tx + " y: " + ty + " z: " + tz);
					} else {
						//System.out.println("No hit");
					}
				}
			}
		}

		tickTimer.update();
	}

	public void render() {
		// world.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(ChunkRenderer::update);

		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());

		GL11.glClearColor(0.52f, 0.8f, 0.92f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		// GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		/*GL11.glEnable(GL11.GL_FOG);
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
		GL11.glFogf(GL11.GL_FOG_START, 128.0f);
		GL11.glFogf(GL11.GL_FOG_END, 1024.0f);*/

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.f, (float) Display.getWidth() / (float) Display.getHeight(), 0.01f, 1000f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GLU.gluLookAt(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(player.pitch, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(player.yaw, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(-player.x, -(player.y + 1.5f), -player.z);

		textureManager.bindTexture("/grass.png");
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		world.getAllLoadedChunks().stream().map(Chunk::getRenderer).forEach(ChunkRenderer::render);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_FOG);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0.0f, Display.getWidth(), Display.getHeight(), 0.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(Display.getWidth() / 2 - 2.0f, Display.getHeight() / 2 - 2.0f);
		GL11.glVertex2f(Display.getWidth() / 2 - 2.0f, Display.getHeight() / 2 + 2.0f);
		GL11.glVertex2f(Display.getWidth() / 2 + 2.0f, Display.getHeight() / 2 + 2.0f);
		GL11.glVertex2f(Display.getWidth() / 2 + 2.0f, Display.getHeight() / 2 - 2.0f);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void tick() {
		Matrix4x3f movementMatrix = new Matrix4x3f();

		movementMatrix.rotate((float) deg2rad(-player.yaw), 0.0f, 1.0f, 0.0f);

		Vector3f movementVector = new Vector3f(0.0f, 0.0f, 0.0f);

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			movementVector.add(0.0f, 0.0f, 0.3f);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			movementVector.add(-0.3f, 0.0f, 0.0f);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			movementVector.add(0.3f, 0.0f, 0.0f);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			movementVector.add(0.0f, 0.0f, -0.3f);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && player.onGround) {
			player.velY += 0.5f;
		}

		movementMatrix.transformDirection(movementVector);

		player.moveClipped(movementVector.x, 0, movementVector.z);

		world.getEntities().stream().forEach(Entity::update);
	}
}
