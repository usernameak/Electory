package tinycraft.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import tinycraft.TinyCraft;
import tinycraft.block.Block;
import tinycraft.render.Tessellator;

public class GuiInGame extends Gui {

	public GuiInGame(TinyCraft tc) {
		super(tc);
	}

	private final Block[] pickableBlocks = new Block[] { Block.blockStone, Block.blockGrass, Block.blockPlanks, Block.blockDirt, null,
			null, null, null, null, null };

	@Override
	public void renderGui(ResolutionScaler scaler) {
		tc.textureManager.bindTexture("/hot_bar.png");
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		// System.out.println(scaler.getHeight());
		Tessellator.instance.getBuffer().addQuadVertexWithUV(0f, scaler.getHeight() - 18, 0f, 0f, 0f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(0f, scaler.getHeight(), 0f, 0f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(162, scaler.getHeight(), 0f, 1f, 1f);
		Tessellator.instance.getBuffer().addQuadVertexWithUV(162, scaler.getHeight() - 18, 0f, 1f, 0f);
		Tessellator.instance.draw();

		for (int i = 0; i < 9; i++) {
			if (pickableBlocks[i] != null) {
				GL11.glPushMatrix();
				GL11.glTranslatef(i * 18, scaler.getHeight() - 18, 0);

				if (tc.player.selectedBlock == pickableBlocks[i]) {
					tc.textureManager.bindTexture("/hot_bar_selection.png");
					Tessellator.instance.getBuffer().addQuadVertexWithUV(0, 0, 0f, 0f, 0f);
					Tessellator.instance.getBuffer().addQuadVertexWithUV(0, 18, 0f, 0f, 1f);
					Tessellator.instance.getBuffer().addQuadVertexWithUV(18, 18, 0f, 1f, 1f);
					Tessellator.instance.getBuffer().addQuadVertexWithUV(18, 0, 0f, 1f, 0f);
					Tessellator.instance.draw();
				}

				GL11.glTranslatef(1, 1, 0);
				pickableBlocks[i].getRenderer().renderBlockInGUI(pickableBlocks[i]);
				GL11.glPopMatrix();
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
			}
		}
	}

}
