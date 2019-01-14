package electory.client.gui.screen;

import org.lwjgl.input.Keyboard;

import electory.client.TinyCraft;
import electory.client.gui.FontRenderer;
import electory.client.gui.GuiRenderState;
import electory.client.gui.IActionListener;
import electory.client.gui.widget.GuiColumnLayout;
import electory.client.gui.widget.GuiRect;
import electory.client.gui.widget.GuiRootContainer;
import electory.client.gui.widget.GuiRootContainer.Position;
import electory.client.gui.widget.GuiWidget;
import electory.utils.Rect2D;

public class GuiConsole extends GuiWidgetScreen implements IActionListener {
	
	public  static final int KEY_TILDE      = 41;
	private static final int CONSOLE_WIDTH  = 800; // TODO: Get from the screen's width
	private static final int CONSOLE_HEIGHT = CONSOLE_WIDTH / 4;

	private   Rect2D	consoleArea = new Rect2D(0, 0, CONSOLE_WIDTH, CONSOLE_HEIGHT);
	protected GuiRect	consoleRect;
	protected String	consoleInputString = "";
	// protected String[]	consoleInput; // TODO:

	public GuiConsole(TinyCraft tc) {
		super(tc);
	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState) {
		if (eventKeyState) {
			switch(eventKey) {
				case GuiConsole.KEY_TILDE:
				case Keyboard.KEY_ESCAPE:
					tc.openGui(null);
					return;
				case Keyboard.KEY_A:
					consoleInputString += "a";
					break;
				case Keyboard.KEY_RETURN:
					// TODO: Send consoleInputString somewhere
					consoleInputString = "";
					break;
			}
		}

		super.handleKeyEvent(eventKey, eventKeyState);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public GuiWidget createRootWidget() {
		GuiColumnLayout layout = new GuiColumnLayout(tc, 5);
		
		consoleRect = new GuiRect(tc, CONSOLE_WIDTH, CONSOLE_HEIGHT);
		layout.add(consoleRect);
		
		GuiRootContainer rootContainer = new GuiRootContainer(tc, layout);
		
		rootContainer.position		= Position.TOP_LEFT;
		rootContainer.verticalGap	= 0;
		
		return rootContainer;
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		super.renderGui(rs);

		tc.fontRenderer.drawTextArea(rs, "I'm a console =)", 0, 0, consoleArea);

		tc.fontRenderer.drawTextArea(rs, "> " + consoleInputString, 0, consoleArea.getMaxY() - FontRenderer.CHAR_HEIGHT, consoleArea);
	}

	@Override
	public void actionPerformed(GuiWidget widget) {}
}