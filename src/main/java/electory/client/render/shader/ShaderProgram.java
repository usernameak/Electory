package electory.client.render.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderProgram {
	protected int handle;

	public ShaderProgram(int... shaders) throws ShaderCompileException {
		handle = GL20.glCreateProgram();
		for (int shader : shaders) {
			GL20.glAttachShader(handle, shader);
			GL20.glAttachShader(handle, shader);
		}
		GL20.glLinkProgram(handle);
		if (GL20.glGetProgrami(handle, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
			ShaderCompileException ex = new ShaderCompileException(
					GL20.glGetProgramInfoLog(handle, GL20.glGetProgrami(handle, GL20.GL_INFO_LOG_LENGTH)));
			GL20.glDeleteProgram(handle);
			throw ex;
		}
	}
	
	public void use() {
		GL20.glUseProgram(handle);
	}
	
	public static void unuse() {
		GL20.glUseProgram(0);
	}
}
