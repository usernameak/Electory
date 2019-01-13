package electory.client.gui;

import org.joml.Matrix4d;

import electory.client.render.IRenderState;

public class GuiRenderState implements IRenderState {
	public ResolutionScaler scaler;
	public Matrix4d projectionMatrix = new Matrix4d();
	public Matrix4d viewMatrix = new Matrix4d();
	public Matrix4d modelMatrix = new Matrix4d();
	
	public GuiRenderState(GuiRenderState orig) {
		super();
		this.scaler = orig.scaler;
		this.projectionMatrix.set(orig.projectionMatrix);
		this.viewMatrix.set(orig.viewMatrix);
		this.modelMatrix.set(orig.modelMatrix);
	}

	public GuiRenderState(ResolutionScaler scaler, Matrix4d projectionMatrix, Matrix4d viewMatrix, Matrix4d modelMatrix) {
		super();
		this.scaler = scaler;
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = viewMatrix;
		this.modelMatrix = modelMatrix;
	}

	public GuiRenderState() {
		super();
	}

	public Matrix4d getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4d getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public Matrix4d getModelMatrix() {
		return modelMatrix;
	}
}
