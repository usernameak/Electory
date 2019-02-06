package electory.client.gui.screen;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.gui.FontRenderer;
import electory.client.gui.GuiRenderState;
import electory.client.gui.ResolutionScaler;
import electory.client.gui.widget.GuiWidget;
import electory.item.ItemStack;

public abstract class GuiWidgetScreen extends GuiScreen {

	public GuiWidgetScreen(TinyCraft tc) {
		super(tc);
	}

	public abstract GuiWidget createRootWidget();

	private GuiWidget rootContainer;

	protected int mouseX, mouseY;

	@Override
	public void setupGuiElementsForScreenSize(ResolutionScaler scaler) {
		super.setupGuiElementsForScreenSize(scaler);
		if (rootContainer == null) {
			rootContainer = createRootWidget();
		} else {
			rootContainer.relayout();
		}
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		rootContainer.renderGui(rs);
		if (tc.player != null && !tc.player.stackOnCursor.isEmpty()) {
			GuiRenderState rs2 = new GuiRenderState(rs);
			rs2.viewMatrix.mul(rs2.modelMatrix);
			rs2.viewMatrix.translate(mouseX, mouseY, 0f);
			rs2.modelMatrix.identity();

			ItemStack stack = tc.player.stackOnCursor;
			if (stack.item != null && stack.count > 0) {
				stack.item.getRenderer().render(stack, new GuiRenderState(rs2));
				tc.fontRenderer.drawText(	rs2,
											String.valueOf(stack.count),
											30 - tc.fontRenderer.getTextWidth(String.valueOf(stack.count)),
											30 - FontRenderer.CHAR_HEIGHT);
			}

		}
	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState, char keyChar) {
		super.handleKeyEvent(eventKey, eventKeyState, keyChar);
		rootContainer.handleKeyEvent(eventKey, eventKeyState, keyChar);
	}

	@Override
	public void handleMouseEvent(MouseEvent event) {
		super.handleMouseEvent(event);
		if (rootContainer != null) {
			rootContainer.handleMouseEvent(event);
			mouseX = event.getX();
			mouseY = event.getY();
		}
	}
}
