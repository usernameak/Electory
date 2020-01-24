package electory.client.gui.screen;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;

import electory.client.KeyEvent;
import electory.client.TinyCraft;
import electory.client.gui.IActionListener;
import electory.client.gui.widget.GuiColumnLayout;
import electory.client.gui.widget.GuiMenuButton;
import electory.client.gui.widget.GuiRootContainer;
import electory.client.gui.widget.GuiRootContainer.Position;
import electory.client.gui.widget.GuiWidget;

public class GuiMainMenu extends GuiWidgetScreen implements IActionListener {

	public GuiMainMenu(TinyCraft tc) {
		super(tc);
	}

	@Override
	public void handleKeyEvent(KeyEvent event) {
		if (event.isKeyState()) {
			if (event.getKey() == GLFW.GLFW_KEY_ESCAPE) {
				tc.openGui(null);
				return;
			}
		}
		super.handleKeyEvent(event);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	protected GuiMenuButton spButton;
	protected GuiMenuButton quitButton;
	
	@Override
	public void openGuiScreen() {
		super.openGuiScreen();
		tc.soundManager.playMusic("mus/main_menu_1.xm", "main_menu_music", true);
	}
	
	@Override
	public void closeGuiScreen() {
		super.closeGuiScreen();
		tc.soundManager.stopMusic("main_menu_music");
	}

	@Override
	public GuiWidget createRootWidget() {
		GuiColumnLayout layout = new GuiColumnLayout(tc, 5);
		spButton = new GuiMenuButton(tc, "Singleplayer", 200, this);
		layout.add(spButton);
		quitButton = new GuiMenuButton(tc, "Quit game", 200, this);
		layout.add(quitButton);
		GuiRootContainer rootContainer = new GuiRootContainer(tc, layout);
		rootContainer.position = Position.BOTTOM_LEFT;
		rootContainer.bgColor = new Color(0xFF404040);
		rootContainer.verticalGap = 50;
		return rootContainer;
	}

	@Override
	public void actionPerformed(GuiWidget widget) {
		if (widget == spButton) {
			tc.openGui(new GuiSaveManager(tc));
		} else if (widget == quitButton) {
			tc.shutdown();
		}
	}
}