package electory.client.render;

import org.joml.Matrix4d;

public interface IRenderState {
	Matrix4d getProjectionMatrix();
	Matrix4d getViewMatrix();

	Matrix4d getModelMatrix();
}
