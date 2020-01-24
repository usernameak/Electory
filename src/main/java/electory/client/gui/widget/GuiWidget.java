package electory.client.gui.widget;

import electory.client.KeyEvent;
import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.gui.Gui;

public abstract class GuiWidget extends Gui implements IRelayoutable {

	protected Gui parent;

	public GuiWidget(TinyCraft tc) {
		super(tc);
	}
	
	public void setParent(Gui parent) {
		this.parent = parent;
	}

	public Gui getParent() {
		return parent;
	}

	public abstract int getWidth();
	
	public abstract int getHeight();
	
	@Override
	public void handleMouseEvent(MouseEvent event) {
		
	}
	
	@Override
	public void handleKeyEvent(KeyEvent event) {
		
	}
}
