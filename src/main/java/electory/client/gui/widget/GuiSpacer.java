package electory.client.gui.widget;

import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;

public class GuiSpacer extends GuiWidget {

	private int width;
	private int height;

	public GuiSpacer(TinyCraft tc, int width, int height) {
		super(tc);
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		// nothing
	}

	@Override
	public void relayout(int width, int height) {
		// nothing
	}

	@Override
	public void handleTextInputEvent(String text) {
		// TODO Auto-generated method stub
		
	}

}
