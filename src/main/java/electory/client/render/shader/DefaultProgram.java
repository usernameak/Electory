package electory.client.render.shader;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import electory.client.TinyCraft;
import electory.client.render.IRenderState;

public class DefaultProgram extends ShaderProgram {
	private int projectionUniform;
	private int viewUniform;
	private int textureUniform;
	private int timerUniform;
	private int modelUniform;

	public static final int POSITION_ATTRIB = 0;
	public static final int TEXCOORD_ATTRIB = 1;
	public static final int COLOR_ATTRIB = 2;

	public DefaultProgram(int... shaders) throws ShaderCompileException {
		super(shaders);
		GL20.glBindAttribLocation(handle, 0, "position");
		GL20.glBindAttribLocation(handle, 1, "texCoord");
		GL20.glBindAttribLocation(handle, 2, "color");
		textureUniform = GL20.glGetUniformLocation(handle, "texture");
		modelUniform = GL20.glGetUniformLocation(handle, "modelMatrix");
		viewUniform = GL20.glGetUniformLocation(handle, "viewMatrix");
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
	
	public void setViewMatrix(Matrix4f matrix) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		matrix.get(fb);
		GL20.glUniformMatrix4(viewUniform, false, fb);
	}
	
	public void setModelMatrix(Matrix4f matrix) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		matrix.get(fb);
		GL20.glUniformMatrix4(modelUniform, false, fb);
	}

	public void setTimer(float timer) {
		GL20.glUniform1f(timerUniform, timer);
	}
	
	public void loadRenderState(IRenderState rs) {
		setProjectionMatrix(rs.getProjectionMatrix());
		setViewMatrix(rs.getViewMatrix());
		setModelMatrix(rs.getModelMatrix());
	}


}
