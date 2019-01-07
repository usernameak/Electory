package tinycraft.client.gui;

import org.joml.Matrix4f;

import tinycraft.client.render.IRenderState;

public class GuiRenderState implements IRenderState {
	public ResolutionScaler scaler;
	public Matrix4f projectionMatrix = new Matrix4f();
	public Matrix4f modelViewMatrix = new Matrix4f();
	
	public GuiRenderState(GuiRenderState orig) {
		super();
		this.scaler = orig.scaler;
		this.projectionMatrix.set(orig.projectionMatrix);
		this.modelViewMatrix.set(orig.modelViewMatrix);
	}

	public GuiRenderState(ResolutionScaler scaler, Matrix4f projectionMatrix, Matrix4f modelViewMatrix) {
		super();
		this.scaler = scaler;
		this.projectionMatrix = projectionMatrix;
		this.modelViewMatrix = modelViewMatrix;
	}

	public GuiRenderState() {
		super();
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4f getModelViewMatrix() {
		return modelViewMatrix;
	}
}
