package electory.client.gui.widget;

import org.lwjgl.opengl.GL11;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;
import electory.client.gui.IActionListener;
import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;

public class GuiMenuButton extends GuiButton implements IActionListener {

	private int width;
	
	private boolean hovered = false;
	
	private IActionListener actionListener;

	public GuiMenuButton(TinyCraft tc, String title, int width) {
		super(tc, title);
		this.width = width;
	}
	
	public GuiMenuButton(TinyCraft tc, String title, int width, IActionListener actionListener) {
		this(tc, title, width);
		this.actionListener = actionListener;
	}
	
	@Override
	public int getWidth() {
		return width + 10;
	}
	
	@Override
	public int getHeight() {
		return super.getHeight() + 10;
	}
	
	@Override
	public void renderGui(GuiRenderState rs) {
		ShaderManager.solidProgram.use();
		ShaderManager.solidProgram.loadRenderState(rs);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator.instance.getBuffer().setColor(hovered ? 0x30FFFFFF : 0x10FFFFFF);
		Tessellator.instance.getBuffer().addQuadVertex(0, 0, 0);
		Tessellator.instance.getBuffer().addQuadVertex(0, getHeight(), 0);
		Tessellator.instance.getBuffer().addQuadVertex(getWidth() - (hovered ? 0 : 5), getHeight(), 0);
		Tessellator.instance.getBuffer().addQuadVertex(getWidth() - (hovered ? 0 : 5), 0, 0);
		Tessellator.instance.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		GL11.glDisable(GL11.GL_BLEND);
		tc.fontRenderer.drawText(rs, title, (hovered ? 10 : 5), 5);
		
	}

	@Override
	public void actionPerformed(GuiWidget widget) {
		if(actionListener != null) {
			actionListener.actionPerformed(widget);
		}
	}
	
	@Override
	public void handleMouseEvent(MouseEvent event) {
		super.handleMouseEvent(event);
		hovered = event.isHovered();
		if(hovered && event.getButtonState() && event.getButton() == 0) {
			actionPerformed(this);
		}
	}
	
}
