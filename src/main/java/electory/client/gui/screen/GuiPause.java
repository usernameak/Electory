package electory.client.gui.screen;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import electory.client.TinyCraft;
import electory.client.gui.IActionListener;
import electory.client.gui.widget.GuiColumnLayout;
import electory.client.gui.widget.GuiMenuButton;
import electory.client.gui.widget.GuiRootContainer;
import electory.client.gui.widget.GuiRootContainer.Position;
import electory.client.gui.widget.GuiWidget;

public class GuiPause extends GuiWidgetScreen implements IActionListener {

	public GuiPause(TinyCraft tc) {
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

	protected GuiMenuButton resumeButton;
	protected GuiMenuButton saveButton;
	protected GuiMenuButton optionsButton;
	protected GuiMenuButton saveAndQuitButton;

	@Override
	public GuiWidget createRootWidget() {
		GuiColumnLayout layout = new GuiColumnLayout(tc, 5);
		resumeButton = new GuiMenuButton(tc, "Resume game", 200, this);
		layout.add(resumeButton);
		saveButton = new GuiMenuButton(tc, "Save game", 200, this);
		layout.add(saveButton);
		optionsButton = new GuiMenuButton(tc, "Options", 200, this);
		layout.add(optionsButton);
		saveAndQuitButton = new GuiMenuButton(tc, "Save and quit", 200, this);
		layout.add(saveAndQuitButton);
		GuiRootContainer rootContainer = new GuiRootContainer(tc, layout);
		rootContainer.position = Position.BOTTOM_LEFT;
		rootContainer.verticalGap = 50;
		return rootContainer;
	}

	@Override
	public void actionPerformed(GuiWidget widget) {
		if (widget == resumeButton) {
			tc.openGui(null);
			return;
		} else if (widget == saveButton) {
			try {
				tc.world.save();
				tc.openGui(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		} else if (widget == optionsButton) {
			// TODO
		} else if (widget == saveAndQuitButton) {
			tc.shutdown();
		}
	}
}