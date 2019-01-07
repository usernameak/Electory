package tinycraft.client.render.world;

import org.joml.Matrix4f;

import tinycraft.client.render.IRenderState;

public class WorldRenderState implements IRenderState {
	public Matrix4f projectionMatrix = new Matrix4f();
	public Matrix4f modelViewMatrix = new Matrix4f();
	
	public WorldRenderState() {
	}
	
	public WorldRenderState(IRenderState orig) {
		projectionMatrix.set(orig.getProjectionMatrix());
		modelViewMatrix.set(orig.getModelViewMatrix());
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4f getModelViewMatrix() {
		return modelViewMatrix;
	}
}
