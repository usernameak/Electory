package electory.client.gui.widget;

import electory.client.TinyCraft;
import electory.client.event.KeyEvent;
import electory.client.gui.FontRenderer;
import electory.client.gui.GuiRenderState;

public class GuiButton extends GuiWidget {

	protected String title;

	public GuiButton(TinyCraft tc, String title) {
		super(tc);
		this.title = title;
	}

	@Override
	public int getWidth() {
		return tc.fontRenderer.getTextWidth(this.title);
	}

	@Override
	public int getHeight() {
		return FontRenderer.CHAR_HEIGHT;
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		tc.fontRenderer.drawText(rs, title, 0, 0);
	}

	@Override
	public void handleKeyEvent(KeyEvent event) {
	}

	@Override
	public void relayout(int width, int height) {
	}

	@Override
	public void handleTextInputEvent(String text) {
		// TODO Auto-generated method stub
		
	}
}
