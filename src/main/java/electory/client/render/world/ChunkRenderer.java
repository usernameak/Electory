package electory.client.render.world;

import java.nio.BufferOverflowException;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;

import electory.world.World;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import electory.block.Block;
import electory.client.TinyCraft;
import electory.client.render.Tessellator;
import electory.client.render.TriangleBuffer;
import electory.client.render.shader.DefaultProgram;
import electory.client.render.shader.ShaderManager;
import electory.client.render.texture.TextureManager;
import electory.profiling.ElectoryProfiler;
import electory.world.Chunk;

public class ChunkRenderer {
	private int[] vbos = new int[WorldRenderer.VBO_COUNT];
	private int[] vaos = new int[WorldRenderer.VBO_COUNT];
	// private TriangleBuffer qb = new TriangleBuffer();
	final Chunk chunk;
	private int[] triangleCounts = new int[vbos.length];
	private int query = 0;

	public static final int[] lightColors = new int[] { 0xff0f0f0f, 0xff1f1f1f, 0xff2f2f2f, 0xff3f3f3f, 0xff4f4f4f,
			0xff5f5f5f, 0xff6f6f6f, 0xff7f7f7f, 0xff8f8f8f, 0xff9f9f9f, 0xffafafaf, 0xffbfbfbf, 0xffcfcfcf, 0xffdfdfdf,
			0xffefefef, 0xffffffff };

	public static final int[] lightColorsBlock = new int[] { 0xff0f0f0f, 0xff1f1f1f, 0xff2f2f2f, 0xff3f3f3f, 0xff4f4f4f,
			0xff5f5f5f, 0xff6f6f6f, 0xff7f7f7f, 0xff8f8f8f, 0xff9f9f9f, 0xffafafaf, 0xffbfbfbf, 0xffcfcfcf, 0xffdfdfdf,
			0xffefefef, 0xffffffff };

	public volatile boolean needsUpdate = true;
	public volatile boolean updateInProgress = false;
	private volatile boolean hasFirstUpdate = false;
	// private volatile boolean isDirty = false;

	private boolean isInitialized = false;

	private volatile TriangleBuffer[] sentBuffers = null;

	public ChunkRenderer(Chunk chunk) {
		this.chunk = chunk;
	}

	public Chunk getChunk() {
		return chunk;
	}

	private void init() {
		if (isInitialized)
			return;
		for (int i = 0; i < vbos.length; i++) {
			vbos[i] = GL15.glGenBuffers();
			vaos[i] = ARBVertexArrayObject.glGenVertexArrays();
			ARBVertexArrayObject.glBindVertexArray(vaos[i]);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[i]);
			GL20.glEnableVertexAttribArray(DefaultProgram.POSITION_ATTRIB);
			GL20.glEnableVertexAttribArray(DefaultProgram.TEXCOORD_ATTRIB);
			GL20.glEnableVertexAttribArray(DefaultProgram.COLOR_ATTRIB);
			GL20.glVertexAttribPointer(DefaultProgram.POSITION_ATTRIB, 3, GL11.GL_FLOAT, false, 24, 0L);
			GL20.glVertexAttribPointer(DefaultProgram.TEXCOORD_ATTRIB, 2, GL11.GL_FLOAT, false, 24, 12L);
			GL20.glVertexAttribPointer(DefaultProgram.COLOR_ATTRIB, 4, GL11.GL_UNSIGNED_BYTE, true, 24, 20L);
			// GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, triangleCounts[vbo] * 3);
			ARBVertexArrayObject.glBindVertexArray(0);
		}
		
		query = GL15.glGenQueries();
		isInitialized = true;
		// update();
	}

	public void doChunkQuery(WorldRenderState rs) {
		init();
		GL11.glColorMask(false, false, false, false);
		GL15.glBeginQuery(GL15.GL_SAMPLES_PASSED, query);
		ShaderManager.solidProgram.use();
		ShaderManager.solidProgram.loadRenderState(rs);
		GL11.glDisable(GL11.GL_CULL_FACE);
		TriangleBuffer buf = Tessellator.instance.getBuffer();
		/*
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, 0);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, 0);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, 0 + 16.0f);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, 0 + 16.0f);
		 * 
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, 0);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, 0);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, 0 + 16.0f);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, 0 + 16.0f);
		 * 
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, 0);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, 0);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, 0);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, 0);
		 * 
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, 0 + 16.0f);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, 0 + 16.0f);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, 0 + 16.0f);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, 0 + 16.0f);
		 * 
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, 0);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 256f, 0 + 16.0f);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, 0 + 16.0f);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX() + 16.0f, 0f, 0);
		 * 
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, 0);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 256f, 0 + 16.0f);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, 0 + 16.0f);
		 * buf.addQuadVertex(getChunk().getChunkBlockCoordX(), 0f, 0);
		 */
		buf.addQuadVertex(0, 0f, 0);
		buf.addQuadVertex(0 + 16.0f, 0f, 0);
		buf.addQuadVertex(0 + 16.0f, 0f, 0 + 16.0f);
		buf.addQuadVertex(0, 0f, 0 + 16.0f);

		buf.addQuadVertex(0, 256f, 0);
		buf.addQuadVertex(0 + 16.0f, 256f, 0);
		buf.addQuadVertex(0 + 16.0f, 256f, 0 + 16.0f);
		buf.addQuadVertex(0, 256f, 0 + 16.0f);

		buf.addQuadVertex(0, 256f, 0);
		buf.addQuadVertex(0 + 16.0f, 256f, 0);
		buf.addQuadVertex(0 + 16.0f, 0f, 0);
		buf.addQuadVertex(0, 0f, 0);

		buf.addQuadVertex(0, 256f, 0 + 16.0f);
		buf.addQuadVertex(0 + 16.0f, 256f, 0 + 16.0f);
		buf.addQuadVertex(0 + 16.0f, 0f, 0 + 16.0f);
		buf.addQuadVertex(0, 0f, 0 + 16.0f);

		buf.addQuadVertex(0 + 16.0f, 256f, 0);
		buf.addQuadVertex(0 + 16.0f, 256f, 0 + 16.0f);
		buf.addQuadVertex(0 + 16.0f, 0f, 0 + 16.0f);
		buf.addQuadVertex(0 + 16.0f, 0f, 0);

		buf.addQuadVertex(0, 256f, 0);
		buf.addQuadVertex(0, 256f, 0 + 16.0f);
		buf.addQuadVertex(0, 0f, 0 + 16.0f);
		buf.addQuadVertex(0, 0f, 0);
		Tessellator.instance.draw();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL15.glEndQuery(GL15.GL_SAMPLES_PASSED);
		GL11.glColorMask(true, true, true, true);

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void beginConditionalRender() {
		GL30.glBeginConditionalRender(query, GL30.GL_QUERY_WAIT);
	}

	public void endConditionalRender() {
		GL30.glEndConditionalRender();
	}

	public void update(Executor chunkUpdateExecutor) {
		if (!updateInProgress) {
			if (sentBuffers == null) {
				init();
				updateInProgress = true;
				// isDirty = false;
				// needsUpdate = false;
				TinyCraft.getInstance().chunkUpdCounter++;
				chunkUpdateExecutor.execute(new Runnable() {

					@Override
					public void run() {
						boolean unlockedInstantly = false;
						Lock lock = chunk.renderLock.readLock();
						lock.lock();
						TriangleBuffer[] buffers = new TriangleBuffer[WorldRenderer.VBO_COUNT];
						try {
							for (int i = 0; i < vbos.length; i++) {
								TriangleBuffer qb = new TriangleBuffer();
								int tricount = 0;
								for (int x = 0; x < 16; x++) {
									for (int y = 0; y < 256; y++) {
										for (int z = 0; z < 16; z++) {
											Block block = chunk.getBlockAt(x, y, z);
											if (block != null && block.shouldRenderInVBO(i)) {
												tricount += block.getRenderer()
														.getTriangleCount(	chunk.world,
																			block,
																			ChunkRenderer.this,
																			chunk.getChunkBlockCoordX() + x,
																			y,
																			chunk.getChunkBlockCoordZ() + z,
																			x,
																			z);
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
												qb.setColor(lightColors[chunk.getLightLevelAt(x, y, z, World.LIGHT_LEVEL_TYPE_SKY)]);
												block.getRenderer()
														.getTriangles(	chunk.world,
																		block,
																		ChunkRenderer.this,
																		chunk.getChunkBlockCoordX() + x,
																		y,
																		chunk.getChunkBlockCoordZ() + z,
																		x,
																		z,
																		qb);
											}
										}
									}
								}
								qb.getBuffer().flip();
								buffers[i] = qb;
							}
						} catch(BufferOverflowException e) {
							TinyCraft.getInstance().logger.warning("buffer overflow in chunk update. that's not okay. reupdating");
							lock.unlock();
							unlockedInstantly = true;
							run();
							return;
						} finally {
							if(!unlockedInstantly) lock.unlock();
						}
						sentBuffers = buffers;
						updateInProgress = false;
					}
				});
			} else {
				for (int i = 0; i < vbos.length; i++) {
					ElectoryProfiler.INSTANCE.begin("chunk_gpu_upload");
					GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[i]);
					GL15.glBufferData(GL15.GL_ARRAY_BUFFER, sentBuffers[i].getBuffer(), GL15.GL_DYNAMIC_DRAW);
					ElectoryProfiler.INSTANCE.end("chunk_gpu_upload");
					// System.out.println("updating buffer took " + (endTime - startTime) + " nanoseconds");
				}
				sentBuffers = null;
				needsUpdate = false;
				hasFirstUpdate = true;
				/*if (isDirty) {
					update(chunkUpdateExecutor); // force a new update because dirty again
				}*/
			}
		}
	}

	public void render(WorldRenderState rs, Executor chunkUpdateExecutor, DefaultProgram shader, int pass, int vbo) {
		init();

		if (needsUpdate) {
			update(chunkUpdateExecutor);
		}

		if (needsUpdate && !hasFirstUpdate)
			return; // not renderable
		
		if(pass == WorldRenderer.RENDERPASS_WIREFRAME) {
			GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
			GL11.glPolygonOffset(-1, -1);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}

		shader.use();
		shader.bindTexture(TextureManager.TERRAIN_TEXTURE);
		shader.loadRenderState(rs);
		ARBVertexArrayObject.glBindVertexArray(vaos[vbo]);
		if (triangleCounts[vbo] != 0) {
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, triangleCounts[vbo] * 3);
		}
		ARBVertexArrayObject.glBindVertexArray(0);
		
		/*GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos[vbo]);
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
		*/
		GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
		
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	public void destroy() {
		for (int i = 0; i < vbos.length; i++) {
			GL15.glDeleteBuffers(vbos[i]);
		}
		GL15.glDeleteQueries(query);
	}

	public void markDirty() {
		needsUpdate = true;
	}
}
