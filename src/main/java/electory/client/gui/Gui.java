package electory.client.gui;

import electory.client.MouseEvent;
import electory.client.TinyCraft;

public abstract class Gui {
	protected final TinyCraft tc;

	public Gui(TinyCraft tc) {
		super();
		this.tc = tc;
	}

	public abstract void renderGui(GuiRenderState rs);

	public abstract void handleKeyEvent(int eventKey, boolean eventKeyState, char keyChar);

	public abstract void handleMouseEvent(MouseEvent event);
}
