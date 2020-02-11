package electory.client.gui;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.event.KeyEvent;

public abstract class Gui {
	protected final TinyCraft tc;

	public Gui(TinyCraft tc) {
		super();
		this.tc = tc;
	}

	public abstract void renderGui(GuiRenderState rs);

	public abstract void handleKeyEvent(KeyEvent event);

	public abstract void handleMouseEvent(MouseEvent event);

	public abstract void handleTextInputEvent(String text);
}
