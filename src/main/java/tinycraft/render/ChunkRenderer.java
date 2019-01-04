package tinycraft.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import tinycraft.block.Block;
import tinycraft.world.Chunk;

public class ChunkRenderer {
	private int vbo;
	private TriangleBuffer qb = new TriangleBuffer();
	private final Chunk chunk;
	private int triangleCount;
	
	public boolean needsUpdate = true;

	public ChunkRenderer(Chunk chunk) {
		this.chunk = chunk;
	}

	public void init() {
		vbo = GL15.glGenBuffers();
		update();
	}

	public void update() {
		needsUpdate = false;
		int tricount = 0;
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 256; y++) {
				for (int z = 0; z < 16; z++) {
					Block block = chunk.getBlockAt(x, y, z);
					if (block != null) {
						tricount += block.getRenderer()
								.getTriangleCount(chunk.world, this, chunk.getChunkBlockCoordX()
										+ x, y, chunk.getChunkBlockCoordZ() + z);
					}
				}
			}
		}
		triangleCount = tricount;
		qb.allocate(tricount);
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 256; y++) {
				for (int z = 0; z < 16; z++) {
					Block block = chunk.getBlockAt(x, y, z);
					if (block != null) {
						block.getRenderer()
								.getTriangles(chunk.world, this, chunk.getChunkBlockCoordX()
										+ x, y, chunk.getChunkBlockCoordZ() + z, qb);
					}
				}
			}
		}
		qb.getBuffer().flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, qb.getBuffer(), GL15.GL_DYNAMIC_DRAW);
	}
	
	public void render() {
		if(needsUpdate) {
			update();
		}
		GL11.glPushMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 24, 0L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 24, 12L);
		GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 24, 20L);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, triangleCount * 3);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glPopMatrix();
	}
}
