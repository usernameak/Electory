package electory.client.gui;

import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import electory.block.Block;
import electory.client.TinyCraft;
import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;

public class GuiInGame extends Gui {

	public GuiInGame(TinyCraft tc) {
		super(tc);
	}

	private final Block[] pickableBlocks = new Block[] { Block.blockCobblestone, Block.blockGrass, Block.blockPlanks,
			Block.blockDirt, Block.blockGlass, Block.blockLog, Block.blockLeaves, Block.blockStone, Block.blockSand };

	@Override
	public void renderGui(GuiRenderState rs) {
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.loadRenderState(rs);
		ShaderManager.defaultProgram.bindTexture("/gui/xhair.png");
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);

		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		Tessellator.instance.getBuffer()
				.addQuadVertexWithUV(rs.scaler.getWidth() / 2 - 2f, rs.scaler.getHeight() / 2 - 2f, 0f, 0f, 0f);
		Tessellator.instance.getBuffer()
				.addQuadVertexWithUV(rs.scaler.getWidth() / 2 - 2f, rs.scaler.getHeight() / 2 + 3f, 0f, 0f, 1f);
		Tessellator.instance.getBuffer()
				.addQuadVertexWithUV(rs.scaler.getWidth() / 2 + 3f, rs.scaler.getHeight() / 2 + 3f, 0f, 1f, 1f);
		Tessellator.instance.getBuffer()
				.addQuadVertexWithUV(rs.scaler.getWidth() / 2 + 3f, rs.scaler.getHeight() / 2 - 2f, 0f, 1f, 0f);
		Tessellator.instance.draw();
		GL11.glDisable(GL11.GL_BLEND);

		ShaderManager.defaultProgram.bindTexture("/gui/hot_bar.png");
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
			if (pickableBlocks[i] != null) {
				Matrix4f m = new Matrix4f(rs.viewMatrix).translate(startX + 4 + i * 40, rs.scaler.getHeight() - 38, 0);
				// GL11.glTranslatef(i * 18, rs.scaler.getHeight() - 18, 0);
				
				GuiRenderState rs2 = new GuiRenderState(rs.scaler, rs.projectionMatrix, m, rs.modelMatrix);

				
				pickableBlocks[i].getRenderer()
						.renderBlockInGUI(pickableBlocks[i], new GuiRenderState(rs2));
				ShaderManager.defaultProgram.loadRenderState(rs2);

				if (tc.player.selectedBlock == pickableBlocks[i]) {
					ShaderManager.defaultProgram.bindTexture("/gui/hot_bar_selection.png");
					Tessellator.instance.getBuffer().addQuadVertexWithUV(-5f, -5f, 0f, 0f, 0f);
					Tessellator.instance.getBuffer().addQuadVertexWithUV(-5f, 37f, 0f, 0f, 1f);
					Tessellator.instance.getBuffer().addQuadVertexWithUV(37f, 37f, 0f, 1f, 1f);
					Tessellator.instance.getBuffer().addQuadVertexWithUV(37f, -5f, 0f, 1f, 0f);
					Tessellator.instance.draw();
				}
				
			}
		}

		tc.fontRenderer.drawText(rs, tc.fps + " FPS, " + tc.chunkUpdates + " chunk updates", 2, 2);
		if (tc.world != null) {
			tc.fontRenderer.drawText(rs, "seed: " + tc.world.seed, 2, 10);
			tc.fontRenderer.drawText(rs, tc.world.getAllLoadedChunks().size() + " chunks loaded", 2, 34);
		}
		if (tc.player != null) {
			Vector3f pos = tc.player.getInterpolatedPosition(0f);
			tc.fontRenderer.drawText(	rs,
										"x: " + (int) Math.floor(pos.x) + ", y: " + (int) Math.floor(pos.y) + ", z: "
												+ (int) Math.floor(pos.z),
										2,
										18);
			if (tc.world != null) {
				tc.fontRenderer.drawText(rs, "biome: " + tc.world.getBiomeAt((int) Math.floor(pos.x), (int) Math.floor(pos.z)).toString(), 2, 26);
			}
		}
	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState) {
		super.handleKeyEvent(eventKey, eventKeyState);

		if (eventKeyState) {
			if (eventKey >= Keyboard.KEY_1 && eventKey <= Keyboard.KEY_9) {
				int i = eventKey - Keyboard.KEY_1;
				if (pickableBlocks[i] != null) {
					tc.player.selectedBlock = pickableBlocks[i];
				}
			} else if(eventKey == Keyboard.KEY_F12) {
				TinyCraft.getInstance().worldRenderer.debugShadows = !TinyCraft.getInstance().worldRenderer.debugShadows;
			} else if(eventKey == Keyboard.KEY_F2) {
				try {
					TinyCraft.getInstance().world.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
