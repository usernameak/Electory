package tinycraft.client.gui;

import java.io.IOException;

import org.joml.Matrix4f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import tinycraft.block.Block;
import tinycraft.client.TinyCraft;
import tinycraft.client.render.Tessellator;
import tinycraft.client.render.shader.ShaderManager;

public class GuiInGame extends Gui {

	public GuiInGame(TinyCraft tc) {
		super(tc);
	}

	private final Block[] pickableBlocks = new Block[] { Block.blockStone, Block.blockGrass, Block.blockPlanks,
			Block.blockDirt, Block.blockGlass, Block.blockLog, Block.blockLeaves, Block.blockWater, Block.blockSand, null };

	@Override
	public void renderGui(GuiRenderState rs) {
		ShaderManager.defaultProgram.use();
		ShaderManager.defaultProgram.setModelViewMatrix(rs.modelViewMatrix);
		ShaderManager.defaultProgram.setProjectionMatrix(rs.projectionMatrix);
		ShaderManager.defaultProgram.bindTexture("/xhair.png");
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);

		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(rs.scaler.getWidth() / 2 - 2.5f, rs.scaler.getHeight() / 2 - 2.5f, 0f, 0f, 0f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(rs.scaler.getWidth() / 2 - 2.5f, rs.scaler.getHeight() / 2 + 2.5f, 0f, 0f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(rs.scaler.getWidth() / 2 + 2.5f, rs.scaler.getHeight() / 2 + 2.5f, 0f, 1f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(rs.scaler.getWidth() / 2 + 2.5f, rs.scaler.getHeight() / 2 - 2.5f, 0f, 1f, 0f);
		Tessellator.instance.draw();
		GL11.glDisable(GL11.GL_BLEND);
		
		ShaderManager.defaultProgram.bindTexture("/hot_bar.png");
		// GL11.glEnable(GL11.GL_TEXTURE_2D);
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		// System.out.println(scaler.getHeight());
		Tessellator.instance.getBuffer().addQuadVertexWithUV(0f, rs.scaler.getHeight() - 18, 0f, 0f, 0f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(0f, rs.scaler.getHeight(), 0f, 0f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(162, rs.scaler.getHeight(), 0f, 1f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(162, rs.scaler.getHeight() - 18, 0f, 1f, 0f);
		Tessellator.instance.draw();

		for (int i = 0; i < 9; i++) {
			if (pickableBlocks[i] != null) {
				Matrix4f m = new Matrix4f(rs.modelViewMatrix).translate(i * 18, rs.scaler.getHeight() - 18, 0);
				// GL11.glTranslatef(i * 18, rs.scaler.getHeight() - 18, 0);

				ShaderManager.defaultProgram.setModelViewMatrix(m);

				if (tc.player.selectedBlock == pickableBlocks[i]) {
					ShaderManager.defaultProgram.bindTexture("/hot_bar_selection.png");
					Tessellator.instance.getBuffer().addQuadVertexWithUV(0, 0, 0f, 0f, 0f);
					Tessellator.instance.getBuffer().addQuadVertexWithUV(0, 18, 0f, 0f, 1f);
					Tessellator.instance.getBuffer().addQuadVertexWithUV(18, 18, 0f, 1f, 1f);
					Tessellator.instance.getBuffer().addQuadVertexWithUV(18, 0, 0f, 1f, 0f);
					Tessellator.instance.draw();
				}

				m.translate(1, 1, 0);
				ShaderManager.defaultProgram.setModelViewMatrix(m);
				pickableBlocks[i].getRenderer()
						.renderBlockInGUI(pickableBlocks[i], new GuiRenderState(rs.scaler, rs.projectionMatrix, m));
			}
		}
		
		tc.fontRenderer.drawText(rs, tc.fps + " FPS", 2, 2);
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
			} else if (eventKey == Keyboard.KEY_F2) {
				try {
					TinyCraft.getInstance().world.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (eventKey == Keyboard.KEY_F3) {
				try {
					TinyCraft.getInstance().world.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
