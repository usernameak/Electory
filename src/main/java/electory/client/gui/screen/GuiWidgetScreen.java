package electory.client.gui.screen;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;
import electory.client.gui.ResolutionScaler;
import electory.client.gui.widget.GuiWidget;

public abstract class GuiWidgetScreen extends GuiScreen {

	public GuiWidgetScreen(TinyCraft tc) {
		super(tc);
	}

	public abstract GuiWidget createRootWidget();

	private GuiWidget rootContainer;

	@Override
	public void setupGuiElementsForScreenSize(ResolutionScaler scaler) {
		super.setupGuiElementsForScreenSize(scaler);
		rootContainer = createRootWidget();
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		rootContainer.renderGui(rs);
	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState, char keyChar) {
		super.handleKeyEvent(eventKey, eventKeyState, keyChar);
		rootContainer.handleKeyEvent(eventKey, eventKeyState, keyChar);
	}
	
	@Override
	public void handleMouseEvent(MouseEvent event) {
		super.handleMouseEvent(event);
		rootContainer.handleMouseEvent(event);
	}
}
