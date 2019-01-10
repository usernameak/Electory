package electory.client.gui;

import org.joml.Matrix4f;

import electory.client.render.IRenderState;

public class GuiRenderState implements IRenderState {
	public ResolutionScaler scaler;
	public Matrix4f projectionMatrix = new Matrix4f();
	public Matrix4f viewMatrix = new Matrix4f();
	public Matrix4f modelMatrix = new Matrix4f();
	
	public GuiRenderState(GuiRenderState orig) {
		super();
		this.scaler = orig.scaler;
		this.projectionMatrix.set(orig.projectionMatrix);
		this.viewMatrix.set(orig.viewMatrix);
		this.modelMatrix.set(orig.modelMatrix);
	}

	public GuiRenderState(ResolutionScaler scaler, Matrix4f projectionMatrix, Matrix4f viewMatrix, Matrix4f modelMatrix) {
		super();
		this.scaler = scaler;
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = viewMatrix;
		this.modelMatrix = modelMatrix;
	}

	public GuiRenderState() {
		super();
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}
}
