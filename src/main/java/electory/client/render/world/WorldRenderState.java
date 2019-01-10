package electory.client.render.world;

import org.joml.Matrix4f;

import electory.client.render.IRenderState;

public class WorldRenderState implements IRenderState {
	public Matrix4f projectionMatrix = new Matrix4f();
	public Matrix4f viewMatrix = new Matrix4f();
	public Matrix4f modelMatrix = new Matrix4f();
	
	public WorldRenderState() {
	}
	
	public WorldRenderState(IRenderState orig) {
		projectionMatrix.set(orig.getProjectionMatrix());
		viewMatrix.set(orig.getViewMatrix());
		modelMatrix.set(orig.getModelMatrix());
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	@Override
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
}
