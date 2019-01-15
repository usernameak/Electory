package electory.client.gui.widget;

import org.lwjgl.opengl.GL11;

import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;
import electory.client.gui.IActionListener;
import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;

public class GuiRect extends GuiWidget {

	private int width;
	private int height;

	public GuiRect(TinyCraft tc, int width, int height) {
		super(tc);
		this.width  = width;
		this.height = height;
	}
	
	@Override
	public int getWidth() {
		return width + 10;
	}
	
	@Override
	public int getHeight() {
		return height + 10;
	}
	
	@Override
	public void renderGui(GuiRenderState rs) {
		ShaderManager.solidProgram.use();
		ShaderManager.solidProgram.loadRenderState(rs);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator.instance.getBuffer().setColor(0x10FFFFFF);
		Tessellator.instance.getBuffer().addQuadVertex(0, 0, 0);
		Tessellator.instance.getBuffer().addQuadVertex(0, getHeight(), 0);
		Tessellator.instance.getBuffer().addQuadVertex(getWidth(), getHeight(), 0);
		Tessellator.instance.getBuffer().addQuadVertex(getWidth(), 0, 0);
		Tessellator.instance.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		GL11.glDisable(GL11.GL_BLEND);
		
	}
}
