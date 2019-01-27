package electory.client.gui.widget;

import org.lwjgl.opengl.GL11;

import electory.client.TinyCraft;
import electory.client.gui.FontRenderer;
import electory.client.gui.GuiRenderState;
import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;
import electory.inventory.IContainerProvider;
import electory.item.ItemStack;

public class GuiWidgetInventoryCell extends GuiWidget {

	private IContainerProvider container;
	private int slot;

	public GuiWidgetInventoryCell(TinyCraft tc, IContainerProvider container, int slot) {
		super(tc);
		this.container = container;
		this.slot = slot;
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
		
		ItemStack stack = container.getItemInSlot(slot); 
		GuiRenderState rs2 = new GuiRenderState(rs);
		rs2.viewMatrix.mul(rs2.modelMatrix);
		rs2.viewMatrix.translate(1f, 1f, 0f);
		rs2.modelMatrix.identity();
		
		if (stack.item != null && stack.count > 0) {
			stack.item.getRenderer().render(stack, new GuiRenderState(rs2));
			tc.fontRenderer.drawText(	rs2,
			                         	String.valueOf(stack.count),
										30 - tc.fontRenderer.getTextWidth(String.valueOf(stack.count)),
										30 - FontRenderer.CHAR_HEIGHT);
		}

	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState, char keyChar) {
		// TODO:
		
	}

	@Override
	public void relayout() {
	}
	
	
}
