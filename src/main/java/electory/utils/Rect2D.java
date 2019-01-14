package electory.utils;

public class Rect2D { // TODO: Move out of here to the separated file
	private int x		= 0;
	private int y		= 0;
	private int width	= 0;
	private int height	= 0;

	public Rect2D(int x, int y, int width, int height) {
		this.x = x; this.y = y; this.width = width; this.height = height;
	}

	// Getters

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getMaxX() {
		return x + width;
	}

	public int getMaxY() {
		return y + height;
	}

	public int getCenterX() {
		return x + (width / 2);
	}

	public int getCenterY() {
		return y + (height / 2);
	}

	// Setters

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	// Functions
}