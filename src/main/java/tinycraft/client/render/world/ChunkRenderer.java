package tinycraft.client.render.world;

import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.NVConditionalRender;

import tinycraft.block.Block;
import tinycraft.client.render.Tessellator;
import tinycraft.client.render.TriangleBuffer;
import tinycraft.client.render.shader.DefaultProgram;
import tinycraft.client.render.shader.ShaderManager;
import tinycraft.world.Chunk;

public class ChunkRenderer {
	private int[] vbos = new int[WorldRenderer.RENDERPASS_LAST];
	private TriangleBuffer qb = new TriangleBuffer();
	private final Chunk chunk;
	private int[] triangleCounts = new int[vbos.length];
	private int query = 0;

	public boolean needsUpdate = true;

	public ChunkRenderer(Chunk chunk) {
		this.chunk = chunk;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public void init() {
		for (int i = 0; i < vbos.length; i++) {
			vbos[i] = GL15.glGenBuffers();
		}
		query = ARBOcclusionQuery.glGenQueriesARB();
		update();
	}
	
	public void doChunkQuery(WorldRenderState rs) {
		GL11.glColorMask(false, false, false, false);
		ARBOcclusionQuery.glBeginQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB, query);
		ShaderManager.whiteProgram.use();
		ShaderManager.whiteProgram.loadRenderState(rs);
		GL11.glDisable(GL11.GL_CULL_FACE);
		TriangleBuffer buf = Tessellator.instance.getBuffer();
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, getChunk().getChunkBlockCoordZ());
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, getChunk().getChunkBlockCoordZ());
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, getChunk().getChunkBlockCoordZ() + 16.0f);
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, getChunk().getChunkBlockCoordZ() + 16.0f);
		
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, getChunk().getChunkBlockCoordZ());
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, getChunk().getChunkBlockCoordZ());
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, getChunk().getChunkBlockCoordZ() + 16.0f);
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, getChunk().getChunkBlockCoordZ() + 16.0f);
		
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, getChunk().getChunkBlockCoordZ());
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, getChunk().getChunkBlockCoordZ());
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, getChunk().getChunkBlockCoordZ());
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, getChunk().getChunkBlockCoordZ());

		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, getChunk().getChunkBlockCoordZ() + 16.0f);
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, getChunk().getChunkBlockCoordZ() + 16.0f);
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, getChunk().getChunkBlockCoordZ() + 16.0f);
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, getChunk().getChunkBlockCoordZ() + 16.0f);

		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, getChunk().getChunkBlockCoordZ());
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, getChunk().getChunkBlockCoordZ() + 16.0f);
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, getChunk().getChunkBlockCoordZ() + 16.0f);
		buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, getChunk().getChunkBlockCoordZ());

		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, getChunk().getChunkBlockCoordZ());
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, getChunk().getChunkBlockCoordZ() + 16.0f);
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, getChunk().getChunkBlockCoordZ() + 16.0f);
		buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, getChunk().getChunkBlockCoordZ());
		Tessellator.instance.draw();
		GL11.glEnable(GL11.GL_CULL_FACE);
		ARBOcclusionQuery.glEndQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB);
		GL11.glColorMask(true, true, true, true);
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void beginConditionalRender() {
		NVConditionalRender.glBeginConditionalRenderNV(query, NVConditionalRender.GL_QUERY_WAIT_NV);
	}
	
	public void endConditionalRender() {
		NVConditionalRender.glEndConditionalRenderNV();
	}

	public void update() {
		needsUpdate = false;
		for (int pass = 0; pass < vbos.length; pass++) {
			int tricount = 0;
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 256; y++) {
					for (int z = 0; z < 16; z++) {
						Block block = chunk.getBlockAt(x, y, z);
						if (block != null && block.shouldRenderInPass(pass)) {
							tricount += block.getRenderer()
									.getTriangleCount(chunk.world, block, this, chunk.getChunkBlockCoordX()
											+ x, y, chunk.getChunkBlockCoordZ() + z);
						}
					}
				}
			}
			triangleCounts[pass] = tricount;
			qb.allocate(tricount);
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 256; y++) {
					for (int z = 0; z < 16; z++) {
						Block block = chunk.getBlockAt(x, y, z);
						if (block != null && block.shouldRenderInPass(pass)) {
							block.getRenderer()
									.getTriangles(chunk.world, block, this, chunk.getChunkBlockCoordX()
											+ x, y, chunk.getChunkBlockCoordZ() + z, qb);
						}
					}
				}
			}
			qb.getBuffer().flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[pass]);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, qb.getBuffer(), GL15.GL_DYNAMIC_DRAW);
			qb.reset();
		}
	}

	public void render(WorldRenderState rs, DefaultProgram shader, int pass) {
		if (needsUpdate) {
			update();
		}

		shader.use();
		shader.bindTexture("/terrain.png");
		shader.setProjectionMatrix(rs.projectionMatrix);
		shader.setModelViewMatrix(rs.modelViewMatrix);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[pass]);
		GL20.glEnableVertexAttribArray(DefaultProgram.POSITION_ATTRIB);
		GL20.glEnableVertexAttribArray(DefaultProgram.TEXCOORD_ATTRIB);
		GL20.glEnableVertexAttribArray(DefaultProgram.COLOR_ATTRIB);
		GL20.glVertexAttribPointer(DefaultProgram.POSITION_ATTRIB, 3, GL11.GL_FLOAT, false, 24, 0L);
		GL20.glVertexAttribPointer(DefaultProgram.TEXCOORD_ATTRIB, 2, GL11.GL_FLOAT, false, 24, 12L);
		GL20.glVertexAttribPointer(DefaultProgram.COLOR_ATTRIB, 4, GL11.GL_UNSIGNED_BYTE, true, 24, 20L);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, triangleCounts[pass] * 3);
		GL20.glDisableVertexAttribArray(DefaultProgram.POSITION_ATTRIB);
		GL20.glDisableVertexAttribArray(DefaultProgram.TEXCOORD_ATTRIB);
		GL20.glDisableVertexAttribArray(DefaultProgram.COLOR_ATTRIB);
	}

	public void destroy() {
		for (int i = 0; i < vbos.length; i++) {
			GL15.glDeleteBuffers(vbos[i]);
		}
		ARBOcclusionQuery.glDeleteQueriesARB(query);
	}
}
