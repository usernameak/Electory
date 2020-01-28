package electory.world.gen;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import electory.block.Block;
import electory.math.MathUtils;
import electory.world.BiomeGenBase;
import electory.world.Chunk;
import electory.world.ChunkColumn;
import electory.world.ChunkPosition;
import electory.world.ColumnPosition;
import electory.world.IChunkProvider;
import electory.world.IChunkSaveStatusHandler;
import electory.world.World;

public class ChunkGenerator implements IChunkProvider {

	private World world;
	private long seed;

	public ChunkGenerator(World world, long seed) {
		this.world = world;
		this.seed = seed;
	}

	@Override
	public void populate(IChunkProvider provider, int x, int y, int z) {

	}

	@Override
	public Chunk loadChunkSynchronously(int cx, int cy, int cz) {
		VoronoiGenerator gen = new VoronoiGenerator(seed ^ 0x1337L, 128);
		PerlinGenerator pgen = new PerlinGenerator(seed ^ 0xCAFEBABE, 8, 3, 0.5);
		PerlinGenerator rgen = new PerlinGenerator(seed ^ 0xDEADBEEF, 6, 2, 0.5);

		short[] chunkData = new short[World.CHUNK_SIZE * World.CHUNK_SIZE * World.CHUNK_SIZE];
		final byte[] biomeData = new byte[World.CHUNK_SIZE * World.CHUNK_SIZE];

		int[][] pArr = new int[World.CHUNK_SIZE + World.CHUNK_SIZE - 1][World.CHUNK_SIZE + World.CHUNK_SIZE - 1];

		for (int i = 0; i < World.CHUNK_SIZE + World.CHUNK_SIZE - 1; i++) {
			for (int j = 0; j < World.CHUNK_SIZE + World.CHUNK_SIZE - 1; j++) {
				int ii = i - World.CHUNK_SIZE / 2;
				int jj = j - World.CHUNK_SIZE / 2;
				int x = (cx << World.CHUNK_BITSHIFT_SIZE) + ii;
				int z = (cz << World.CHUNK_BITSHIFT_SIZE) + jj;

				double val1 = pgen.generate(x / 64.0, z / 64.0);
				double val1c = val1;
				if (val1c > 1)
					val1c = 1;
				else if (val1c < 0)
					val1c = 0;
				double val = gen.generate(x, z, (int) (val1 * 256) - 128);
				pArr[i][j] = val < 0.5 ? 64 : 32;
			}
		}
		int[][] psums = MathUtils.doPartialSums(pArr);

		int sandId = world.blockIdRegistry.getBlockId(Block.REGISTRY.get("sand"));
		int grassId = world.blockIdRegistry.getBlockId(Block.REGISTRY.get("grass"));
		int waterId = world.blockIdRegistry.getBlockId(Block.REGISTRY.get("water"));
		
		int cMinY = cy << World.CHUNK_BITSHIFT_SIZE;
		int cMaxY = cMinY + World.CHUNK_SIZE;
		// System.out.println(cMinY);

		for (int i = 0; i < World.CHUNK_SIZE; i++) {
			for (int j = 0; j < World.CHUNK_SIZE; j++) {
				int x = (cx << World.CHUNK_BITSHIFT_SIZE) | i;
				int z = (cz << World.CHUNK_BITSHIFT_SIZE) | j;

				int totalBiomeHeightVal = MathUtils.getRectangleSum(psums, i, j, World.CHUNK_SIZE, World.CHUNK_SIZE)
						/ (World.CHUNK_SIZE * World.CHUNK_SIZE);

				double relief = rgen.generate(x / 64.0, z / 64.0);

				int heightOffset = (int) (relief * 16);

				int maxY = (int) (heightOffset + totalBiomeHeightVal);
				// System.out.println(maxY);

				for (int y = cMinY; y < maxY && y < cMaxY; y++) {
					int y1 = y - cMinY;
					
					chunkData[(i << World.CHUNK_BITSHIFT_SIZE << World.CHUNK_BITSHIFT_SIZE)
							| (j << World.CHUNK_BITSHIFT_SIZE)
							| (y1)] = maxY < 64 ? (short) sandId : (short) grassId;
				}
				/*for (int y = maxY; y < 64; y++) {
					chunkData[(i << World.CHUNK_BITSHIFT_SIZE << World.CHUNK_BITSHIFT_SIZE)
							| (j << World.CHUNK_BITSHIFT_SIZE)
							| (z)] = (byte) waterId;
				}*/
			}
		}

		Chunk chunk = new Chunk(world, cx, cy, cz);

		for (int i = 0; i < World.CHUNK_SIZE; i++) {
			for (int j = 0; j < World.CHUNK_SIZE; j++) {
				for (int z = 0; z < World.CHUNK_SIZE; z++) {
					chunk.setBlockAt(	i,
										z,
										j,
										world.blockIdRegistry
												.getBlockById(chunkData[(i << World.CHUNK_BITSHIFT_SIZE << World.CHUNK_BITSHIFT_SIZE)
														| (j << World.CHUNK_BITSHIFT_SIZE)
														| (z)]),
										World.FLAG_SKIP_UPDATE);
				}
				chunk.setBiomeAt(i, j, BiomeGenBase.biomeList[biomeData[j << World.CHUNK_BITSHIFT_SIZE | i]]);
			}
		}
		
		chunk.recalculateSkyLight();
		chunk.buildHeightMap();

		return chunk;
	}

	@Override
	public void save(IChunkSaveStatusHandler statusHandler, Chunk chunk) {
	}

	@Override
	public boolean canLoadChunk(int cx, int cy, int cz) {
		return true;
	}

	@Override
	public void waitUntilAllSaved() {
	}

	@Override
	public void reset() {
	}

	@Override
	public Chunk provideChunk(int cx, int cy, int cz) {
		return null;
	}

	@Override
	public Collection<Chunk> getAllLoadedChunks() {
		return null;
	}

	@Override
	public boolean isChunkLoaded(int x, int y, int z) {
		return true; // don't ask wtf
	}

	@Override
	public void unloadChunk(Chunk chunk, Iterator<Chunk> it, boolean doSave) {
	}

	@Override
	public void coldUnloadAll() {
	}

	@Override
	public Map<ChunkPosition, Chunk> getLoadedChunkMap() {
		return null;
	}

	@Override
	public void loadChunk(int cx, int cy, int cz) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ChunkColumn provideColumn(int cx, int cz) {
		return null;
	}

	@Override
	public void save(ChunkColumn chunkColumn) {
		
	}

	@Override
	public boolean canLoadColumn(int cx, int cz) {
		return true;
	}

	@Override
	public Collection<ChunkColumn> getAllLoadedColumns() {
		return null;
	}

	@Override
	public Map<ColumnPosition, ChunkColumn> getLoadedColumnMap() {
		return null;
	}

	@Override
	public boolean isColumnLoaded(int x, int z) {
		return true;
	}

	@Override
	public void unloadColumn(ChunkColumn chunk, Iterator<ChunkColumn> it, boolean doSave) {
		
	}

	@Override
	public ChunkColumn loadColumnSynchronously(int cx, int cz) {
		return new ChunkColumn(world, cx, cz);
	}
	
	@Override
	public void loadColumn(int cx, int cz) {
		throw new UnsupportedOperationException();
	}
}
