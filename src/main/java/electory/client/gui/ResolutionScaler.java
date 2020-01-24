package electory.client.gui;

import org.lwjgl.glfw.GLFW;

import electory.client.TinyCraft;

public class ResolutionScaler {
	public float scale;

	public ResolutionScaler(float scale) {
		this.scale = scale;
	}
	
	public void setupOrtho(GuiRenderState renderState) {
		renderState.scaler = this;
		renderState.projectionMatrix.identity();
		int[] w = new int[1];
		int[] h = new int[1];
		GLFW.glfwGetFramebufferSize(TinyCraft.getInstance().window, w, h);
		renderState.projectionMatrix.ortho2D(0f, w[0] / scale, h[0] / scale, 0f);
		renderState.viewMatrix.identity();
		renderState.modelMatrix.identity();
	}
	
	
	public int getWidth() {
		int[] w = new int[1];
		int[] h = new int[1];
		GLFW.glfwGetFramebufferSize(TinyCraft.getInstance().window, w, h);
		return (int) (w[0] / scale);
	}
	
	public int getHeight() {
		int[] w = new int[1];
		int[] h = new int[1];
		GLFW.glfwGetFramebufferSize(TinyCraft.getInstance().window, w, h);
		return (int) (h[0] / scale);
	}
}
