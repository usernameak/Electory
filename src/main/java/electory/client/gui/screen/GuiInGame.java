package electory.client.gui.screen;

import java.io.IOException;
import java.util.Map;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.event.KeyEvent;
import electory.client.gui.FontRenderer;
import electory.client.gui.GuiRenderState;
import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;
import electory.item.ItemStack;
import electory.profiling.ElectoryProfiler;

public class GuiInGame extends GuiScreen {

	public GuiInGame(TinyCraft tc) {
		super(tc);
	}

	/*
	 * private final Block[] pickableBlocks = new Block[] { Block.blockCobblestone,
	 * Block.blockGrass, Block.blockPlanks, Block.blockDirt, Block.blockGlass,
	 * Block.blockLog, Block.blockSapling, Block.blockStone, Block.blockSand };
	 */

	@Override
	public void renderGui(GuiRenderState rs) {
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.loadRenderState(rs);
		ShaderManager.defaultProgram.bindTexture("/img/hud/xhair.png");
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);

		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(rs.scaler.getWidth() / 2 - 2f, rs.scaler.getHeight() / 2 - 2f, 0f, 0f, 0f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(rs.scaler.getWidth() / 2 - 2f, rs.scaler.getHeight() / 2 + 3f, 0f, 0f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(rs.scaler.getWidth() / 2 + 3f, rs.scaler.getHeight() / 2 + 3f, 0f, 1f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(rs.scaler.getWidth() / 2 + 3f, rs.scaler.getHeight() / 2 - 2f, 0f, 1f, 0f);
		Tessellator.instance.draw();
		GL11.glDisable(GL11.GL_BLEND);

		ShaderManager.defaultProgram.bindTexture("/img/hud/hot_bar.png");
		// GL11.glEnable(GL11.GL_TEXTURE_2D);
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);

		float startX = rs.scaler.getWidth() / 2 - 180f;
		// System.out.println(scaler.getHeight());
		Tessellator.instance.getBuffer().addQuadVertexWithUV(startX, rs.scaler.getHeight() - 42, 0f, 0f, 0f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(startX, rs.scaler.getHeight() - 2, 0f, 0f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(startX + 360f, rs.scaler.getHeight() - 2, 0f, 1f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(startX + 360f, rs.scaler.getHeight() - 42, 0f, 1f, 0f);
		Tessellator.instance.draw();

		for (int i = 0; i < 9; i++) {
			ItemStack stack = tc.player.inventory.getStackInSlot(i);
			Matrix4d m = new Matrix4d(rs.viewMatrix).translate(startX + 4 + i * 40, rs.scaler.getHeight() - 38, 0);

			GuiRenderState rs2 = new GuiRenderState(rs.scaler, rs.projectionMatrix, m, rs.modelMatrix);

			if (stack.item != null && stack.count > 0) {
				stack.item.getRenderer().render(stack, new GuiRenderState(rs2));
				tc.fontRenderer.drawText(rs2, String.valueOf(stack.count), 30 - tc.fontRenderer.getTextWidth(String.valueOf(stack.count)), 30 - FontRenderer.CHAR_HEIGHT);
			}
			
			ShaderManager.defaultProgram.loadRenderState(rs2);

			if (tc.player.inventory.getSelectedSlot() == i) {
				ShaderManager.defaultProgram.bindTexture("/img/hud/hot_bar_selection.png");
				Tessellator.instance.getBuffer().addQuadVertexWithUV(-5f, -5f, 0f, 0f, 0f);
				Tessellator.instance.getBuffer().addQuadVertexWithUV(-5f, 37f, 0f, 0f, 1f);
				Tessellator.instance.getBuffer().addQuadVertexWithUV(37f, 37f, 0f, 1f, 1f);
				Tessellator.instance.getBuffer().addQuadVertexWithUV(37f, -5f, 0f, 1f, 0f);
				Tessellator.instance.draw();
			}
			
		}

		tc.fontRenderer.drawText(rs, "Electory " + TinyCraft.getVersion(), 2, 2);
		tc.fontRenderer.drawText(rs, tc.fps + " FPS, " + tc.chunkUpdates + " chunk updates", 2, 10);
		if (tc.world != null) {
			tc.fontRenderer.drawText(rs, "seed: " + tc.world.seed, 2, 18);
			tc.fontRenderer.drawText(rs, tc.world.chunkProvider.getAllLoadedChunks().size() + " chunks loaded", 2, 50);
		}
		if (tc.player != null) {
			Vector3d pos = tc.player.getInterpolatedPosition(0f);
			tc.fontRenderer.drawText(rs, "x: " + (int) Math.floor(pos.x) + ", y: " + (int) Math.floor(pos.y) + ", z: " + (int) Math.floor(pos.z), 2, 26);
			if (tc.world != null) {
				tc.fontRenderer.drawText(rs, "biome: " + tc.world.getBiomeAt((int) Math.floor(pos.x), (int) Math.floor(pos.z)).toString(), 2, 34);
			}

			Vector3f vel = new Vector3f(tc.player.getVelocity());
			Vector3f svel = new Vector3f();
			tc.player.playerController.doMovement(tc.player, svel);
			vel.add(svel);

			tc.fontRenderer.drawText(rs, "vel: " + String.format("%.3f", vel.length()) + " (x: " + String.format("%.3f", vel.x) + ", y: " + String.format("%.3f", vel.y) + ", z: " + String.format("%.3f", vel.z) + ")", 2, 42);

			tc.fontRenderer.drawText(rs, "snd: " + tc.soundManager.numPlayingSounds(), 2, 58);
		}
		int y = 66;
		for (Map.Entry<String, Long> entry : ElectoryProfiler.INSTANCE.getTimes().entrySet()) {
			tc.fontRenderer.drawText(rs, String.format("%-20s: %dns", entry.getKey(), entry.getValue()), 2, y);
			y += 8;
		}
	}

	@Override
	public void handleKeyEvent(KeyEvent event) {
		if (event.isKeyState()) {
			if (event.getKey() >= GLFW.GLFW_KEY_1 && event.getKey() <= GLFW.GLFW_KEY_9) {
				int i = event.getKey() - GLFW.GLFW_KEY_1;
				tc.player.inventory.setSelectedSlot(i);
			} else if (event.getKey() == GLFW.GLFW_KEY_F12) {
				TinyCraft.getInstance().worldRenderer.wireframeEnabled = !TinyCraft.getInstance().worldRenderer.wireframeEnabled;
			} else if (event.getKey() == GLFW.GLFW_KEY_F2) {
				try {
					TinyCraft.getInstance().world.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (event.getKey() == GLFW.GLFW_KEY_ESCAPE) {
				tc.openGui(new GuiPause(tc));
			} else if (event.getKey() == GuiConsole.KEY_TILDE) {
				tc.openGui(tc.console.gui);
			} else if (event.getKey() == GLFW.GLFW_KEY_E) {
				tc.openGui(new GuiPlayerInventory(tc));
			}
		}
	}

	@Override
	public void handleMouseEvent(MouseEvent event) {
		super.handleMouseEvent(event);

		if (event.getDWheel() != 0) {
			int i = tc.player.inventory.getSelectedSlot();
			i += -event.getDWheel() / 120;
			if (i >= 9) {
				i = 0;
			} else if (i < 0) {
				i = 8;
			}
			tc.player.inventory.setSelectedSlot(i);
		}
	}

	@Override
	public void handleTextInputEvent(String text) {
		// TODO Auto-generated method stub

	}

}
