package tinycraft.gui;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ResolutionScaler {
	public float scale;

	public ResolutionScaler(float scale) {
		this.scale = scale;
	}
	
	public void setupOrtho() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0f, Display.getWidth() / scale, Display.getHeight() / scale, 0f);
		// GL11.glScalef(scale, scale, scale);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	public int getWidth() {
		return (int) (Display.getWidth() / scale);
	}
	
	public int getHeight() {
		return (int) (Display.getHeight() / scale);
	}
}
