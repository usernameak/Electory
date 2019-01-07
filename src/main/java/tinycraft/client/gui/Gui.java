package tinycraft.client.gui;

import tinycraft.client.TinyCraft;

public abstract class Gui {
	protected final TinyCraft tc;
	
	public Gui(TinyCraft tc) {
		super();
		this.tc = tc;
	}

	public abstract void renderGui(GuiRenderState rs);
	
	public void handleKeyEvent(int eventKey, boolean eventKeyState) {
	}
}
