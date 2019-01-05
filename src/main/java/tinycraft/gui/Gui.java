package tinycraft.gui;

import tinycraft.TinyCraft;

public abstract class Gui {
	protected final TinyCraft tc;
	
	public Gui(TinyCraft tc) {
		super();
		this.tc = tc;
	}

	public abstract void renderGui(ResolutionScaler scaler);
	
	public void handleKeyEvent(int eventKey, boolean eventKeyState) {
	}
}
