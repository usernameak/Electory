package electory.client;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import electory.client.gui.ResolutionScaler;

public class MouseEvent {
	private int button;
	private boolean buttonState;
	private int x, y, dx, dy;
	private int dWheel;
	private boolean hovered = true;

	private MouseEvent() {

	}

	private MouseEvent(MouseEvent from) {
		this.button = from.button;
		this.buttonState = from.buttonState;
		this.x = from.x;
		this.y = from.y;
		this.dx = from.dx;
		this.dy = from.dy;
		this.dWheel = from.dWheel;
	}

	public static MouseEvent fromLWJGLEvent() {
		MouseEvent event = new MouseEvent();

		event.button = Mouse.getEventButton();
		event.buttonState = Mouse.getEventButtonState();
		event.x = Mouse.getEventX();
		event.y = Display.getHeight() - Mouse.getEventY();
		event.dx = Mouse.getEventDX();
		event.dy = -Mouse.getEventDY();
		event.dWheel = Mouse.getEventDWheel();

		return event;
	}

	public int getButton() {
		return button;
	}

	public boolean getButtonState() {
		return buttonState;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getDX() {
		return dx;
	}

	public int getDY() {
		return dy;
	}

	public int getDWheel() {
		return dWheel;
	}

	public boolean isHovered() {
		return hovered;
	}

	public MouseEvent adjustToGuiScale(ResolutionScaler scaler) {
		MouseEvent ret = new MouseEvent(this);
		ret.dx /= scaler.scale;
		ret.dy /= scaler.scale;
		ret.x /= scaler.scale;
		ret.y /= scaler.scale;
		return ret;
	}

	public MouseEvent clipEvent(int x, int y, int w, int h) {
		MouseEvent ret = new MouseEvent(this);

		ret.x -= x;
		ret.y -= y;
		ret.hovered = this.hovered && this.x >= x && this.y >= y && this.x < x + w && this.y < y + h;
		
		return ret;
	}
}
