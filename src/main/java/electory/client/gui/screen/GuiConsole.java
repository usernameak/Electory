package electory.client.gui.screen;

import org.lwjgl.input.Keyboard;

import electory.client.TinyCraft;
import electory.client.gui.IActionListener;
import electory.client.gui.widget.GuiColumnLayout;
import electory.client.gui.widget.GuiRect;
import electory.client.gui.widget.GuiRootContainer;
import electory.client.gui.widget.GuiRootContainer.Position;
import electory.client.gui.widget.GuiWidget;

public class GuiConsole extends GuiWidgetScreen implements IActionListener {
	
	public  static final int KEY_TILDE      = 41;
	private static final int CONSOLE_WIDTH  = 800; // TODO: Get from the screen's width
	private static final int CONSOLE_HEIGHT = CONSOLE_WIDTH / 4;

	protected GuiRect consoleRect;

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
	public void actionPerformed(GuiWidget widget) {}
}