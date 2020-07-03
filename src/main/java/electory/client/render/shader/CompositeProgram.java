package electory.client.render.shader;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import electory.client.TinyCraft;

public class CompositeProgram extends DefaultProgram {

	private int waterMaskTextureUniform;
	private int depthTextureUniform;
	private int opaquePosTextureUniform;
	private int submergedUniform;
	private int positionTextureUniform;
	private int cameraPosUniform;
	private int zNearUniform;
	private int zFarUniform;

	public CompositeProgram(int... shaders) throws ShaderCompileException {
		super(shaders);
		waterMaskTextureUniform = GL20.glGetUniformLocation(handle, "watermask_texture");
		depthTextureUniform = GL20.glGetUniformLocation(handle, "depth_texture");
		opaquePosTextureUniform = GL20.glGetUniformLocation(handle, "opaque_pos_texture");
		submergedUniform = GL20.glGetUniformLocation(handle, "isSubmergedUnderwater");
		positionTextureUniform = GL20.glGetUniformLocation(handle, "position_texture");
		zNearUniform = GL20.glGetUniformLocation(handle, "zNear");
		zFarUniform = GL20.glGetUniformLocation(handle, "zFar");
		cameraPosUniform = GL20.glGetUniformLocation(handle, "uCameraPos");
	}

	public void bindTextureDepth(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(depthTextureUniform, 2);
	}

	public void bindTexturePosition(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(positionTextureUniform, 4);
	}

	public void setSubmergedInWater(boolean value) {
		GL20.glUniform1i(submergedUniform, value ? 1 : 0);
	}

	public void setZNear(float value) {
		GL20.glUniform1f(zNearUniform, value);
	}

	public void setCameraPos(Vector3f playerPos) {
		GL20.glUniform3f(cameraPosUniform, playerPos.x, playerPos.y, playerPos.z);
	}

	public void setZFar(float value) {
		GL20.glUniform1f(zFarUniform, value);
	}
}
