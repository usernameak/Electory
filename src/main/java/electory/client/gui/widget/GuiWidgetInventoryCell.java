package electory.client.gui.widget;

import org.lwjgl.opengl.GL11;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.gui.FontRenderer;
import electory.client.gui.GuiRenderState;
import electory.client.render.Tessellator;
import electory.client.render.shader.ShaderManager;
import electory.inventory.IContainerProvider;
import electory.inventory.SlotClickAction;
import electory.item.ItemStack;

public class GuiWidgetInventoryCell extends GuiWidget {

	private IContainerProvider container;
	private int slot;
	
	private boolean isHovered = false;

	public GuiWidgetInventoryCell(TinyCraft tc, IContainerProvider container, int slot) {
		super(tc);
		this.container = container;
		this.slot = slot;
	}

	@Override
	public int getWidth() {
		return 42;
	}

	@Override
	public int getHeight() {
		return 42;
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		ShaderManager.solidProgram.use();
		ShaderManager.solidProgram.loadRenderState(rs);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator.instance.getBuffer().setColor(isHovered ? 0x60FFFFFF : 0x30FFFFFF);
		Tessellator.instance.getBuffer().addQuadVertex(1, 1, 0);
		Tessellator.instance.getBuffer().addQuadVertex(1, 41, 0);
		Tessellator.instance.getBuffer().addQuadVertex(41, 41, 0);
		Tessellator.instance.getBuffer().addQuadVertex(41, 1, 0);
		Tessellator.instance.draw();
		Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		GL11.glDisable(GL11.GL_BLEND);
		/*Tessellator.instance.getBuffer().setColor(0xFFFFFFFF);
		Tessellator.instance.getBuffer().addVertex(1, 1, 0);
		Tessellator.instance.getBuffer().addVertex(1, 34, 0);
		Tessellator.instance.getBuffer().addVertex(1, 34, 0);
		Tessellator.instance.getBuffer().addVertex(34, 34, 0);
		Tessellator.instance.getBuffer().addVertex(34, 34, 0);
		Tessellator.instance.getBuffer().addVertex(34, 1, 0);
		Tessellator.instance.getBuffer().addVertex(34, 1, 0);
		Tessellator.instance.getBuffer().addVertex(1, 1, 0);
		Tessellator.instance.draw(GL11.GL_LINES);*/

		ItemStack stack = container.getItemInSlot(slot);
		GuiRenderState rs2 = new GuiRenderState(rs);
		rs2.viewMatrix.mul(rs2.modelMatrix);
		rs2.viewMatrix.translate(5f, 5f, 0f);
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
	public void handleMouseEvent(MouseEvent event) {
		super.handleMouseEvent(event);
		isHovered = event.isHovered();
		if (event.isHovered()) {
			if (event.getButtonState()) {
				if (event.getButton() == 0) {
					container.slotClicked(tc.player, slot, SlotClickAction.CLICK_ALL);
				} else if(event.getButton() == 1) {
					container.slotClicked(tc.player, slot, SlotClickAction.CLICK_PARTIAL);
				}
			}
		}
	}

	@Override
	public void relayout() {
	}

}
