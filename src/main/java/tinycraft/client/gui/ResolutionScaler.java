package tinycraft.client.gui;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ResolutionScaler {
	public float scale;

	public ResolutionScaler(float scale) {
		this.scale = scale;
	}
	
	@Deprecated
	public void setupOrtho() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0f, Display.getWidth() / scale, Display.getHeight() / scale, 0f);
		// GL11.glScalef(scale, scale, scale);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	public void setupOrtho(GuiRenderState renderState) {
		renderState.scaler = this;
		renderState.projectionMatrix.identity();
		renderState.projectionMatrix.ortho2D(0f, Display.getWidth() / scale, Display.getHeight() / scale, 0f);
		renderState.modelViewMatrix.identity();
	}
	
	
	public int getWidth() {
		return (int) (Display.getWidth() / scale);
	}
	
	public int getHeight() {
		return (int) (Display.getHeight() / scale);
	}
}
