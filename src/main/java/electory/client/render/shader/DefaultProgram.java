package electory.client.render.shader;

import java.nio.FloatBuffer;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import electory.client.BufferPool;
import electory.client.TinyCraft;
import electory.client.render.IRenderState;

public class DefaultProgram extends ShaderProgram {
	private int projectionUniform;
	private int viewUniform;
	private int textureUniform;
	private int timerUniform;
	private int modelUniform;
	private int colorUniform;

	public static final int POSITION_ATTRIB = 0;
	public static final int TEXCOORD_ATTRIB = 1;
	public static final int COLOR_ATTRIB = 2;

	public DefaultProgram(int... shaders) throws ShaderCompileException {
		super(shaders);
		textureUniform = GL20.glGetUniformLocation(handle, "texture");
		modelUniform = GL20.glGetUniformLocation(handle, "modelMatrix");
		viewUniform = GL20.glGetUniformLocation(handle, "viewMatrix");
		projectionUniform = GL20.glGetUniformLocation(handle, "projectionMatrix");
		timerUniform = GL20.glGetUniformLocation(handle, "timer");
		colorUniform = GL20.glGetUniformLocation(handle, "uColor");
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttributes();
		GL20.glBindAttribLocation(handle, 0, "position");
		GL20.glBindAttribLocation(handle, 1, "texCoord");
		GL20.glBindAttribLocation(handle, 2, "color");
	}

	public void bindTexture(String texture) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		TinyCraft.getInstance().textureManager.bindTexture(texture);
		use();
		GL20.glUniform1i(textureUniform, 0);
	}
	
	public void setProjectionMatrix(Matrix4d matrix) {
		FloatBuffer fb = BufferPool.get().getFloatBuffer(16);
		matrix.get(fb);
		GL20.glUniformMatrix4fv(projectionUniform, false, fb);
	}
	
	public void setViewMatrix(Matrix4d matrix) {
		FloatBuffer fb = BufferPool.get().getFloatBuffer(16);
		matrix.get(fb);
		GL20.glUniformMatrix4fv(viewUniform, false, fb);
	}
	
	public void setModelMatrix(Matrix4d matrix) {
		FloatBuffer fb = BufferPool.get().getFloatBuffer(16);
		matrix.get(fb);
		GL20.glUniformMatrix4fv(modelUniform, false, fb);
	}

	public void setTimer(float timer) {
		GL20.glUniform1f(timerUniform, timer);
	}

	public void setColor(float r, float g, float b, float a) {
		GL20.glUniform4f(colorUniform, r, g, b, a);
	}
	
	public void loadRenderState(IRenderState rs) {
		setProjectionMatrix(rs.getProjectionMatrix());
		setViewMatrix(rs.getViewMatrix());
		setModelMatrix(rs.getModelMatrix());
	}


}
