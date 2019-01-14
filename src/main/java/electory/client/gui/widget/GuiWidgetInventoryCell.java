package electory.client.gui.widget;

import org.lwjgl.opengl.GL11;

import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;
import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;

public class GuiWidgetInventoryCell extends GuiWidget {

	public GuiWidgetInventoryCell(TinyCraft tc) {
		super(tc);
	}

	@Override
	public int getWidth() {
		return 34;
	}

	@Override
	public int getHeight() {
		return 34;
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		ShaderManager.solidProgram.use();
		ShaderManager.solidProgram.loadRenderState(rs);
		Tessellator.instance.getBuffer().setColor(0xFF808080);
		Tessellator.instance.getBuffer().addQuadVertex(0, 0, 0);
		Tessellator.instance.getBuffer().addQuadVertex(0, 34, 0);
		Tessellator.instance.getBuffer().addQuadVertex(34, 34, 0);
		Tessellator.instance.getBuffer().addQuadVertex(34, 0, 0);
		Tessellator.instance.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		Tessellator.instance.getBuffer().addVertex(1, 1, 0);
		Tessellator.instance.getBuffer().addVertex(1, 34, 0);
		Tessellator.instance.getBuffer().addVertex(1, 34, 0);
		Tessellator.instance.getBuffer().addVertex(34, 34, 0);
		Tessellator.instance.getBuffer().addVertex(34, 34, 0);
		Tessellator.instance.getBuffer().addVertex(34, 1, 0);
		Tessellator.instance.getBuffer().addVertex(34, 1, 0);
		Tessellator.instance.getBuffer().addVertex(1, 1, 0);
		Tessellator.instance.draw(GL11.GL_LINES);
	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState, char keyChar) {
		// TODO:
		
	}
	
	
}
