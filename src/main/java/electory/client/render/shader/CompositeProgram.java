package electory.client.render.shader;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import electory.client.TinyCraft;

public class CompositeProgram extends DefaultProgram {

	private int waterMaskTextureUniform;
	private int depthTextureUniform;
	private int opaqueDepthTextureUniform;
	private int submergedUniform;
	private int depthShadowTextureUniform;
	
	public CompositeProgram(int... shaders) throws ShaderCompileException {
		super(shaders);
		waterMaskTextureUniform = GL20.glGetUniformLocation(handle, "watermask_texture");
		depthTextureUniform = GL20.glGetUniformLocation(handle, "depth_texture");
		opaqueDepthTextureUniform = GL20.glGetUniformLocation(handle, "opaque_depth_texture");
		submergedUniform = GL20.glGetUniformLocation(handle, "isSubmergedUnderwater");
		depthShadowTextureUniform = GL20.glGetUniformLocation(handle, "depth_shadow_texture");
	}
	
	public void bindTextureWaterMask(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(waterMaskTextureUniform, 1);
	}
	
	public void bindTextureDepth(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(depthTextureUniform, 2);
	}
	
	public void bindTextureOpaqueDepth(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(opaqueDepthTextureUniform, 3);
	}

	public void setSubmergedInWater(boolean value) {
		GL20.glUniform1i(submergedUniform, value ? 1 : 0);
	}

	public void bindTextureDepthShadow(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(depthShadowTextureUniform, 4);
	}

}
