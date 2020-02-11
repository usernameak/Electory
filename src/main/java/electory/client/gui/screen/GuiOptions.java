package electory.client.gui.screen;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;

import electory.client.TinyCraft;
import electory.client.event.KeyEvent;
import electory.client.gui.GuiRenderState;
import electory.client.gui.IActionListener;
import electory.client.gui.widget.GuiColumnLayout;
import electory.client.gui.widget.GuiMenuButton;
import electory.client.gui.widget.GuiRootContainer;
import electory.client.gui.widget.GuiRootContainer.Position;
import electory.client.gui.widget.GuiWidget;
import electory.utils.DrawUtil;

public class GuiOptions extends GuiWidgetScreen implements IActionListener {
	public boolean isMainMenu = false;
	public GuiOptions(TinyCraft tc, boolean isMainMenu) {
		super(tc);
		this.isMainMenu = isMainMenu;
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
		quitButton = new GuiMenuButton(tc, "Return", 200, this);
		layout.add(quitButton);
		GuiRootContainer rootContainer = new GuiRootContainer(tc, layout);
		rootContainer.position = Position.BOTTOM_LEFT;
		if (this.isMainMenu) rootContainer.bgColor = new Color(0xFF404040);
		rootContainer.verticalGap = 50;
		return rootContainer;
	}

	@Override
	public void actionPerformed(GuiWidget widget) {
		if (widget == quitButton) {
			if (!this.isMainMenu) tc.openGui(null);
			else tc.openGui(new GuiMainMenu(tc));
		}
	}
	
	@Override
	public void renderGui(GuiRenderState rs) {
		super.renderGui(rs);
		tc.fontRenderer.drawText(rs, "Options", 10, 10);
	}
}