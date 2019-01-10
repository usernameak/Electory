package electory.client.gui;

import org.lwjgl.opengl.Display;

public class ResolutionScaler {
	public float scale;

	public ResolutionScaler(float scale) {
		this.scale = scale;
	}
	
	public void setupOrtho(GuiRenderState renderState) {
		renderState.scaler = this;
		renderState.projectionMatrix.identity();
		renderState.projectionMatrix.ortho2D(0f, Display.getWidth() / scale, Display.getHeight() / scale, 0f);
		renderState.viewMatrix.identity();
		renderState.modelMatrix.identity();
	}
	
	
	public int getWidth() {
		return (int) (Display.getWidth() / scale);
	}
	
	public int getHeight() {
		return (int) (Display.getHeight() / scale);
	}
}
