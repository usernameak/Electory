package electory.client.gui.screen;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.gui.Gui;
import electory.client.gui.ResolutionScaler;

public abstract class GuiScreen extends Gui {

	public GuiScreen(TinyCraft tc) {
		super(tc);
	}
	
	public void setupGuiElementsForScreenSize(ResolutionScaler scaler) {
		
	}

	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState, char keyChar) {
		
	}
	
	@Override
	public void handleMouseEvent(MouseEvent event) {
		
	}

	public void closeGuiScreen() {
	}

	public void openGuiScreen() {
	}
	
}
