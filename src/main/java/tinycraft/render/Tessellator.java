package tinycraft.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

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
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, getBuffer().getBuffer(), GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 24, 0L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 24, 12L);
		GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 24, 20L);
		// System.out.println(getBuffer().getVertexCount());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, getBuffer().getVertexCount());
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		getBuffer().reset();
	}
}
