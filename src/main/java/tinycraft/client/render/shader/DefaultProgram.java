package tinycraft.client.render.shader;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import tinycraft.client.TinyCraft;
import tinycraft.client.render.IRenderState;

public class DefaultProgram extends ShaderProgram {
	private int projectionUniform;
	private int modelViewUniform;
	private int textureUniform;
	private int timerUniform;

	public static final int POSITION_ATTRIB = 0;
	public static final int TEXCOORD_ATTRIB = 1;
	public static final int COLOR_ATTRIB = 2;

	public DefaultProgram(int... shaders) throws ShaderCompileException {
		super(shaders);
		GL20.glBindAttribLocation(handle, 0, "position");
		GL20.glBindAttribLocation(handle, 1, "texCoord");
		GL20.glBindAttribLocation(handle, 2, "color");
		textureUniform = GL20.glGetUniformLocation(handle, "texture");
		modelViewUniform = GL20.glGetUniformLocation(handle, "modelViewMatrix");
		projectionUniform = GL20.glGetUniformLocation(handle, "projectionMatrix");
		timerUniform = GL20.glGetUniformLocation(handle, "timer");
	}

	public void bindTexture(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(textureUniform, 0);
	}
	
	public void setProjectionMatrix(Matrix4f matrix) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		matrix.get(fb);
		GL20.glUniformMatrix4(projectionUniform, false, fb);
	}
	
	public void setModelViewMatrix(Matrix4f matrix) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		matrix.get(fb);
		GL20.glUniformMatrix4(modelViewUniform, false, fb);
	}

	public void setTimer(float timer) {
		GL20.glUniform1f(timerUniform, timer);
	}
	
	public void loadRenderState(IRenderState rs) {
		setProjectionMatrix(rs.getProjectionMatrix());
		setModelViewMatrix(rs.getModelViewMatrix());
	}


}
