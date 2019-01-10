package electory.client.render;

import org.joml.Matrix4f;

public interface IRenderState {
	Matrix4f getProjectionMatrix();
	Matrix4f getViewMatrix();

	Matrix4f getModelMatrix();
}
