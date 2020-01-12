package electory.client.render;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import electory.client.render.shader.DefaultProgram;

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
	
	private int vao = 0;

	public void draw(int mode) {
		getBuffer().getBuffer().flip();
		if(vao == 0) {
			vao = ARBVertexArrayObject.glGenVertexArrays();
			ARBVertexArrayObject.glBindVertexArray(vao);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL20.glVertexAttribPointer(DefaultProgram.POSITION_ATTRIB, 3, GL11.GL_FLOAT, false, 24, 0L);
			GL20.glVertexAttribPointer(DefaultProgram.TEXCOORD_ATTRIB, 2, GL11.GL_FLOAT, false, 24, 12L);
			GL20.glVertexAttribPointer(DefaultProgram.COLOR_ATTRIB, 4, GL11.GL_UNSIGNED_BYTE, true, 24, 20L);
			GL20.glEnableVertexAttribArray(DefaultProgram.POSITION_ATTRIB);
			GL20.glEnableVertexAttribArray(DefaultProgram.TEXCOORD_ATTRIB);
			GL20.glEnableVertexAttribArray(DefaultProgram.COLOR_ATTRIB);
		} else {
			ARBVertexArrayObject.glBindVertexArray(vao);
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, getBuffer().getBuffer(), GL15.GL_STREAM_DRAW);
		GL11.glDrawArrays(mode, 0, getBuffer().getVertexCount());
		ARBVertexArrayObject.glBindVertexArray(0);
		getBuffer().reset();
	}

	public void draw() {
		draw(GL11.GL_TRIANGLES);
	}
}
