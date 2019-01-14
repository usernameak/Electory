package electory.client.gui.widget;

import java.util.LinkedList;
import java.util.List;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;

public class GuiRowLayout extends GuiWidget {

	private List<GuiWidget> children = new LinkedList<>();

	private int cachedWidth = 0;

	private int cachedHeight = 0;

	public GuiRowLayout(TinyCraft tc) {
		super(tc);
	}

	public void add(GuiWidget widget) {
		widget.setParent(this);
		cachedWidth += widget.getWidth();
		int h = widget.getHeight();
		cachedHeight = h > cachedHeight ? h : cachedHeight;
		children.add(widget);
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		GuiRenderState rs2 = new GuiRenderState(rs);
		for (GuiWidget child : children) {
			child.renderGui(rs2);
			rs2.modelMatrix.translate(child.getWidth(), 0f, 0f);
		}
	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState, char keyChar) {
		children.stream().forEach(c -> c.handleKeyEvent(eventKey, eventKeyState, keyChar));
	}

	@Override
	public void handleMouseEvent(MouseEvent event) {
		// TODO:
		super.handleMouseEvent(event);
	}

	@Override
	public int getWidth() {
		return cachedWidth;
	}

	@Override
	public int getHeight() {
		return cachedHeight;
	}

}
