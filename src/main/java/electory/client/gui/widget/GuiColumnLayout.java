package electory.client.gui.widget;

import java.util.LinkedList;
import java.util.List;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.gui.GuiRenderState;

public class GuiColumnLayout extends GuiWidget {
	
	private List<GuiWidget> children = new LinkedList<>();
	
	private int cachedWidth = 0;
	
	private int cachedHeight = 0;

	private int gap;

	public GuiColumnLayout(TinyCraft tc, int gap) {
		super(tc);
		this.gap = gap;
	}
	
	public void add(GuiWidget widget) {
		widget.setParent(this);
		cachedHeight += children.isEmpty() ? widget.getHeight() : gap + widget.getHeight();
		int w = widget.getWidth();
		cachedWidth = w > cachedWidth ? w : cachedWidth;
		children.add(widget);
	}

	@Override
	public void renderGui(GuiRenderState rs) {
		GuiRenderState rs2 = new GuiRenderState(rs);
		for(GuiWidget child : children) {
			child.renderGui(rs2);
			rs2.modelMatrix.translate(0f, gap + child.getHeight(), 0f);
		}
	}

	@Override
	public void handleKeyEvent(int eventKey, boolean eventKeyState) {
		children.stream().forEach(c -> c.handleKeyEvent(eventKey, eventKeyState));
	}
	
	@Override
	public void handleMouseEvent(MouseEvent event) {
		super.handleMouseEvent(event);
		int y = 0;
		for(GuiWidget child : children) {
			child.handleMouseEvent(event.clipEvent(0, y, child.getWidth(), child.getHeight()));
			y += gap + child.getHeight();
		}
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
