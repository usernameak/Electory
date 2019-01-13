package electory.client.render.shader;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import electory.client.TinyCraft;

public class TerrainProgram extends DefaultProgram {
	private int depthShadowTextureUniform;

	public TerrainProgram(int... shaders) throws ShaderCompileException {
		super(shaders);
		depthShadowTextureUniform = GL20.glGetUniformLocation(handle, "depth_shadow_texture");
	}

	public void bindTextureDepthShadow(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(depthShadowTextureUniform, 1);
	}

}
