package electory.client.render.world;

import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.NVConditionalRender;

import electory.block.Block;
import electory.client.TinyCraft;
import electory.client.render.Tessellator;
import electory.client.render.TriangleBuffer;
import electory.client.render.shader.DefaultProgram;
import electory.client.render.shader.ShaderManager;
import electory.world.Chunk;

public class ChunkRenderer {
	private int[] vbos = new int[WorldRenderer.VBO_COUNT];
	private TriangleBuffer qb = new TriangleBuffer();
	private final Chunk chunk;
	private int[] triangleCounts = new int[vbos.length];
	private int query = 0;

	public static final int[] lightColors = new int[] { 0xff0f0f0f, 0xff1f1f1f, 0xff2f2f2f, 0xff3f3f3f, 0xff4f4f4f,
			0xff5f5f5f, 0xff6f6f6f, 0xff7f7f7f, 0xff8f8f8f, 0xff9f9f9f, 0xffafafaf, 0xffbfbfbf, 0xffcfcfcf, 0xffdfdfdf,
			0xffefefef, 0xffffffff };

	public boolean needsUpdate = true;
	
	private boolean isInitialized = false;

	public ChunkRenderer(Chunk chunk) {
		this.chunk = chunk;
	}

	public Chunk getChunk() {
		return chunk;
	}

	private void init() {
		if(isInitialized) return;
		for (int i = 0; i < vbos.length; i++) {
			vbos[i] = GL15.glGenBuffers();
		}
		query = ARBOcclusionQuery.glGenQueriesARB();
		isInitialized = true;
		update();
	}

	public void doChunkQuery(WorldRenderState rs) {
		init();
		GL11.glColorMask(false, false, false, false);
		ARBOcclusionQuery.glBeginQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB, query);
		ShaderManager.solidProgram.use();
		ShaderManager.solidProgram.loadRenderState(rs);
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
		init();
		needsUpdate = false;
		TinyCraft.getInstance().chunkUpdCounter++;
		for (int i = 0; i < vbos.length; i++) {
			int tricount = 0;
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 256; y++) {
					for (int z = 0; z < 16; z++) {
						Block block = chunk.getBlockAt(x, y, z);
						if (block != null && block.shouldRenderInVBO(i)) {
							tricount += block.getRenderer()
									.getTriangleCount(	chunk.world,
														block,
														this,
														chunk.getChunkBlockCoordX() + x,
														y,
														chunk.getChunkBlockCoordZ() + z);
						}
					}
				}
			}
			triangleCounts[i] = tricount;
			qb.allocate(tricount);
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 256; y++) {
					for (int z = 0; z < 16; z++) {
						Block block = chunk.getBlockAt(x, y, z);
						if (block != null && block.shouldRenderInVBO(i)) {
							qb.setColor(lightColors[chunk.getSunLightLevelAt(x, y, z)]);
							block.getRenderer()
									.getTriangles(	chunk.world,
													block,
													this,
													chunk.getChunkBlockCoordX() + x,
													y,
													chunk.getChunkBlockCoordZ() + z,
													qb);
						}
					}
				}
			}
			qb.getBuffer().flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[i]);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, qb.getBuffer(), GL15.GL_DYNAMIC_DRAW);
			qb.reset();
		}
	}

	public void render(WorldRenderState rs, DefaultProgram shader, int pass, int vbo) {
		init();
		
		if (needsUpdate) {
			update();
		}

		shader.use();
		shader.bindTexture("/terrain.png");
		shader.loadRenderState(rs);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[vbo]);
		GL20.glEnableVertexAttribArray(DefaultProgram.POSITION_ATTRIB);
		GL20.glEnableVertexAttribArray(DefaultProgram.TEXCOORD_ATTRIB);
		GL20.glEnableVertexAttribArray(DefaultProgram.COLOR_ATTRIB);
		GL20.glVertexAttribPointer(DefaultProgram.POSITION_ATTRIB, 3, GL11.GL_FLOAT, false, 24, 0L);
		GL20.glVertexAttribPointer(DefaultProgram.TEXCOORD_ATTRIB, 2, GL11.GL_FLOAT, false, 24, 12L);
		GL20.glVertexAttribPointer(DefaultProgram.COLOR_ATTRIB, 4, GL11.GL_UNSIGNED_BYTE, true, 24, 20L);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, triangleCounts[vbo] * 3);
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
