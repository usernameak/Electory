package electory.client.gui.screen;

import org.lwjgl.input.Keyboard;

import electory.client.TinyCraft;
import electory.client.gui.widget.GuiColumnLayout;
import electory.client.gui.widget.GuiRootContainer;
import electory.client.gui.widget.GuiRowLayout;
import electory.client.gui.widget.GuiSpacer;
import electory.client.gui.widget.GuiWidget;
import electory.client.gui.widget.GuiWidgetInventoryCell;
import electory.client.gui.widget.GuiRootContainer.Position;

public class GuiPlayerInventory extends GuiWidgetScreen {

	public GuiPlayerInventory(TinyCraft tc) {
		super(tc);
	}

	@Override
	public GuiWidget createRootWidget() {
		GuiColumnLayout columnLayout = new GuiColumnLayout(tc, 0);

		for (int i = 0; i < 4; i++) {
			GuiRowLayout rowLayout = new GuiRowLayout(tc, 0);
			for (int j = 0; j < 9; j++) {
				GuiWidgetInventoryCell invCell = new GuiWidgetInventoryCell(tc, tc.player.inventoryContainer, 9 + i * 9 + j);
				rowLayout.add(invCell);
			}
			columnLayout.add(rowLayout);
		}
		
		columnLayout.add(new GuiSpacer(tc, 0, 8));

		{
			GuiRowLayout rowLayout = new GuiRowLayout(tc, 0);
			for (int i = 0; i < 9; i++) {
				GuiWidgetInventoryCell invCell = new GuiWidgetInventoryCell(tc, tc.player.inventoryContainer, i);
				rowLayout.add(invCell);
			}
			columnLayout.add(rowLayout);
		}

		GuiRootContainer root = new GuiRootContainer(tc, columnLayout);
		root.position = Position.CENTER;
		return root;
	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState, char keyChar) {
		super.handleKeyEvent(eventKey, eventKeyState, keyChar);

		if (eventKeyState) {
			if (eventKey == Keyboard.KEY_ESCAPE || eventKey == Keyboard.KEY_E) {
				tc.openGui(null);
			}
		}
	}
}
