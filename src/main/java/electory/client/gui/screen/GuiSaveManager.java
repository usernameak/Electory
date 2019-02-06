package electory.client.gui.screen;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import electory.client.TinyCraft;
import electory.client.gui.IActionListener;
import electory.client.gui.widget.GuiColumnLayout;
import electory.client.gui.widget.GuiMenuButton;
import electory.client.gui.widget.GuiRootContainer;
import electory.client.gui.widget.GuiRootContainer.Position;
import electory.client.gui.widget.GuiWidget;
import electory.utils.CrashException;
import electory.world.WorldSP;

public class GuiSaveManager extends GuiWidgetScreen implements IActionListener {

	public GuiSaveManager(TinyCraft tc) {
		super(tc);
	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState, char keyChar) {
		if (eventKeyState) {
			if (eventKey == Keyboard.KEY_ESCAPE) {
				tc.openGui(null);
				return;
			}
		}
		super.handleKeyEvent(eventKey, eventKeyState, keyChar);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	protected GuiMenuButton newWorldButton;
	protected GuiMenuButton quitButton;

	@Override
	public void openGuiScreen() {
		super.openGuiScreen();
	}

	@Override
	public void closeGuiScreen() {
		super.closeGuiScreen();
	}

	@Override
	public GuiWidget createRootWidget() {
		GuiColumnLayout layout = new GuiColumnLayout(tc, 5);
		File folder = getSPWorldsFolder();
		if (folder.isDirectory()) {
			for (String worldName : folder.list()) {
				layout.add(new GuiMenuButton(tc, worldName, 200) {
					@Override
					public void actionPerformed(GuiWidget widget) {
						super.actionPerformed(widget);
						tc.openGui(null);
						tc.setWorld(new WorldSP(worldName));
						try {
							tc.world.load();
						} catch (IOException e) {
							throw new CrashException(e);
						}
					}
				});
			}
		}
		newWorldButton = new GuiMenuButton(tc, "New world", 200, this);
		layout.add(newWorldButton);
		quitButton = new GuiMenuButton(tc, "Back", 200, this);
		layout.add(quitButton);
		GuiRootContainer rootContainer = new GuiRootContainer(tc, layout);
		rootContainer.position = Position.BOTTOM_LEFT;
		rootContainer.bgColor = new Color(0xFF404040);
		rootContainer.verticalGap = 50;
		return rootContainer;
	}

	protected File getSPWorldsFolder() {
		return new File(tc.getUserDataDir(), "universes");
	}

	@Override
	public void actionPerformed(GuiWidget widget) {
		if (widget == newWorldButton) {
			tc.openGui(null);
			File spFolder = getSPWorldsFolder();
			int i = 0;
			while(new File(spFolder, "universe" + i).exists()) {
				i++;
			}
			
			tc.setWorld(new WorldSP("universe" + i));
			try {
				tc.world.load();
			} catch (IOException e) {
				throw new CrashException(e);
			}
		} else if (widget == quitButton) {
			tc.openGui(new GuiMainMenu(tc));
		}
	}
}