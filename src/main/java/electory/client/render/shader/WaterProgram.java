package electory.client.render.shader;

import org.lwjgl.opengl.GL20;

public class WaterProgram extends DefaultProgram {
	private int waterPositionOffsetUniform;

	public WaterProgram(int... shaders) throws ShaderCompileException {
		super(shaders);
		waterPositionOffsetUniform = GL20.glGetUniformLocation(handle, "waterPositionOffset");
	}

	public void setWaterPositionOffset(float x, float y, float z) {
		use();
		GL20.glUniform3f(waterPositionOffsetUniform, x, y, z);
	}

}
