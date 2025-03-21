package electory.client.gui.screen;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import electory.client.TinyCraft;
import electory.client.event.KeyEvent;
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

	public static final int KEY_TILDE = GLFW.GLFW_KEY_GRAVE_ACCENT;
	private static final int CONSOLE_WIDTH = 800; // TODO: Get from the screen's width
	private static final int CONSOLE_HEIGHT = CONSOLE_WIDTH / 4;

	protected final Rect2D consoleArea = new Rect2D(0, 0, CONSOLE_WIDTH, CONSOLE_HEIGHT - FontRenderer.CHAR_HEIGHT);
	protected final Rect2D consoleInputArea = new Rect2D(0, CONSOLE_HEIGHT, CONSOLE_WIDTH, 600 /* TODO: Screen Height */);
	protected GuiRect consoleRect;
	protected String consoleInputString = "";
	protected ArrayList<String> consoleInput = new ArrayList<>();
	protected ArrayList<String> consoleHistory = new ArrayList<>();
	protected int consoleHistoryIndex = -1; // -1 = ""

	public GuiConsole(TinyCraft tc) {
		super(tc);
	}

	@Override
	public void handleKeyEvent(KeyEvent event) {
		if (event.isKeyState()) {
			switch (event.getKey()) {
			case GuiConsole.KEY_TILDE:
			case GLFW.GLFW_KEY_ESCAPE:
				tc.openGui(null);
				return;
			case GLFW.GLFW_KEY_ENTER:
				println("> " + consoleInputString);

				// If the history isn't empty and the command isn't doublicate of previous
				if (consoleHistory.size() == 0
						|| !consoleHistory.get(consoleHistory.size() - 1).equals(consoleInputString))
					consoleHistory.add(consoleInputString);

				consoleHistoryIndex = consoleHistory.size() - 1;
				tc.console.execCommand(consoleInputString);
				consoleInputString = "";

				break;
			case GLFW.GLFW_KEY_BACKSPACE:
				if (consoleInputString.length() > 0)
					consoleInputString = consoleInputString.substring(0, consoleInputString.length() - 1);
				break;
			case GLFW.GLFW_KEY_UP:
			case GLFW.GLFW_KEY_DOWN:
				if (!consoleHistory.isEmpty()) {
					if (event.getKey() == GLFW.GLFW_KEY_UP)
						consoleHistoryIndex++;
					else if (event.getKey() == GLFW.GLFW_KEY_DOWN)
						consoleHistoryIndex--;

					if (consoleHistoryIndex < 0) { // Latest change
						consoleHistoryIndex = -1;
						consoleInputString = "";
					} else if (consoleHistoryIndex >= consoleHistory.size()) {
						consoleHistoryIndex = 0;
						consoleInputString = consoleHistory.get(consoleHistoryIndex);
					} else {
						consoleInputString = consoleHistory.get(consoleHistoryIndex);
					}
				}
				break;
			/*default:
				if (event.getKeyChar() != '\0') {
					consoleInputString += event.getKeyChar();
				}*/
			}
		}

		super.handleKeyEvent(event);

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

		rootContainer.position = Position.TOP_LEFT;
		rootContainer.verticalGap = 0;

		return rootContainer;
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		super.renderGui(rs);

		int lines = -1;
		int tmpY = consoleInput.size() * FontRenderer.CHAR_HEIGHT;

		for (String console : consoleInput) {
			lines = tc.fontRenderer.getTextLinesArea(console, consoleArea);
			tc.fontRenderer.drawTextArea(rs, console, 0, consoleArea.getMaxY() - FontRenderer.CHAR_HEIGHT - tmpY,
					consoleArea);
			tmpY -= lines * FontRenderer.CHAR_HEIGHT;

			if (tmpY < consoleArea.getY())
				break;
		}

		tc.fontRenderer.drawTextArea(rs, "> " + consoleInputString, 0, consoleArea.getMaxY() + FontRenderer.CHAR_HEIGHT, consoleInputArea);
	}

	@Override
	public void actionPerformed(GuiWidget widget) {
	}

	public void println(String ln) {
		consoleInput.add(ln);
	}

	public void clear() {
		consoleInput.clear();
	}
	
	@Override
	public void closeGuiScreen() {
		super.closeGuiScreen();
		// GLFW.GLFW_enableRepeatEvents(false); // FIXME:
		consoleInputString = "";
	}
	
	@Override
	public void openGuiScreen() {
		super.openGuiScreen();
		// GLFW.GLFW_enableRepeatEvents(true); // FIXME:
	}

	@Override
	public void handleTextInputEvent(String text) {
		consoleInputString += text;
	}
}