package electory.client.render.world;

import org.joml.Matrix4d;

import electory.client.render.IRenderState;

public class WorldRenderState implements IRenderState {
	public Matrix4d projectionMatrix = new Matrix4d();
	public Matrix4d viewMatrix = new Matrix4d();
	public Matrix4d modelMatrix = new Matrix4d();
	
	public WorldRenderState() {
	}
	
	public WorldRenderState(IRenderState orig) {
		projectionMatrix.set(orig.getProjectionMatrix());
		viewMatrix.set(orig.getViewMatrix());
		modelMatrix.set(orig.getModelMatrix());
	}

	@Override
	public Matrix4d getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4d getModelMatrix() {
		return modelMatrix;
	}

	@Override
	public Matrix4d getViewMatrix() {
		return viewMatrix;
	}
}
