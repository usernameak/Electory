package tinycraft.client.render;

import org.joml.Matrix4f;

public interface IRenderState {
	Matrix4f getProjectionMatrix();

	Matrix4f getModelViewMatrix();
}
