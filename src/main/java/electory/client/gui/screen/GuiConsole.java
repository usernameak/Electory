package electory.client.gui.screen;

import java.util.Iterator;
import java.util.Stack;

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

	private   Rect2D		consoleArea = new Rect2D(0, 0, CONSOLE_WIDTH, CONSOLE_HEIGHT - FontRenderer.CHAR_HEIGHT);
	protected GuiRect		consoleRect;
	protected String		consoleInputString = "";
	protected Stack<String> consoleInput;

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
					consoleInput.push(consoleInputString);
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

		// consoleInput.add("Hello! I'm a console =)");
		
		return rootContainer;
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		super.renderGui(rs);

		int tmpy = 0;

		for(String console : consoleInput) {
			tmpy = tc.fontRenderer.drawTextArea(rs, console, 0, tmpy, consoleArea);
			if(tmpy > consoleArea.getMaxY()) break;
		}

		tc.fontRenderer.drawText(rs, "> " + consoleInputString, 0, consoleArea.getMaxY() - FontRenderer.CHAR_HEIGHT);
	}

	@Override
	public void actionPerformed(GuiWidget widget) {}
}