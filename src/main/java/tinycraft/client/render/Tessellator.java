package tinycraft.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import tinycraft.client.render.shader.DefaultProgram;

public class Tessellator {
	public static final Tessellator instance = new Tessellator();

	private TriangleBuffer buffer = new TriangleBuffer();
	private int vbo = GL15.glGenBuffers();

	{
		buffer.allocate(262144);
	}

	public TriangleBuffer getBuffer() {
		return buffer;
	}

	public void draw() {
		getBuffer().getBuffer().flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, getBuffer().getBuffer(), GL15.GL_STREAM_DRAW);
		GL20.glVertexAttribPointer(DefaultProgram.POSITION_ATTRIB, 3, GL11.GL_FLOAT, false, 24, 0L);
		GL20.glVertexAttribPointer(DefaultProgram.TEXCOORD_ATTRIB, 2, GL11.GL_FLOAT, false, 24, 12L);
		GL20.glVertexAttribPointer(DefaultProgram.COLOR_ATTRIB, 4, GL11.GL_UNSIGNED_BYTE, true, 24, 20L);
		GL20.glEnableVertexAttribArray(DefaultProgram.POSITION_ATTRIB);
		GL20.glEnableVertexAttribArray(DefaultProgram.TEXCOORD_ATTRIB);
		GL20.glEnableVertexAttribArray(DefaultProgram.COLOR_ATTRIB);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, getBuffer().getVertexCount());
		GL20.glDisableVertexAttribArray(DefaultProgram.POSITION_ATTRIB);
		GL20.glDisableVertexAttribArray(DefaultProgram.TEXCOORD_ATTRIB);
		GL20.glDisableVertexAttribArray(DefaultProgram.COLOR_ATTRIB);
		getBuffer().reset();
	}
}
