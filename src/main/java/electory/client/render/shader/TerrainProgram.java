package electory.client.render.shader;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import electory.client.TinyCraft;

public class TerrainProgram extends DefaultProgram {
	private int lightUniform;
	private int depthShadowTextureUniform;

	public TerrainProgram(int... shaders) throws ShaderCompileException {
		super(shaders);
		lightUniform = GL20.glGetUniformLocation(handle, "lightMatrix");
		depthShadowTextureUniform = GL20.glGetUniformLocation(handle, "depth_shadow_texture");
	}

	public void setLightMatrix(Matrix4f matrix) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		matrix.get(fb);
		GL20.glUniformMatrix4(lightUniform, false, fb);
	}

	public void bindTextureDepthShadow(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(depthShadowTextureUniform, 1);
	}

}
