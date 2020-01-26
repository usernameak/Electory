package electory.client.gui.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import electory.client.MouseEvent;
import electory.client.TinyCraft;
import electory.client.event.KeyEvent;
import electory.client.gui.GuiRenderState;

public class GuiRowLayout extends GuiWidget {

	private List<GuiWidget> children = new LinkedList<>();

	private int cachedWidth = 0;

	private int cachedHeight = 0;

	private int gap;

	public GuiRowLayout(TinyCraft tc, int gap) {
		super(tc);
		this.gap = gap;
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
	public void handleKeyEvent(KeyEvent event) {
		children.stream().forEach(c -> c.handleKeyEvent(event));
	}

	@Override
	public void handleMouseEvent(MouseEvent event) {
		super.handleMouseEvent(event);
		int x = 0;
		for(GuiWidget child : children) {
			child.handleMouseEvent(event.clipEvent(x, 0, child.getWidth(), child.getHeight()));
			x += gap + child.getWidth();
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

	@Override
	public void relayout(int width, int height) {
		cachedWidth = cachedHeight = 0;
		ListIterator<GuiWidget> it = children.listIterator();
		while(it.hasNext()) {
			// int i = it.nextIndex();
			GuiWidget widget = it.next();
			cachedWidth += widget.getWidth();
			int h = widget.getHeight();
			cachedHeight = h > cachedHeight ? h : cachedHeight;
			children.add(widget);
		}
	}

}
