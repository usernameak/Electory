package electory.world.gen;

import java.util.Collection;
import java.util.Iterator;

import electory.block.Block;
import electory.math.MathUtils;
import electory.world.Chunk;
import electory.world.EnumWorldBiome;
import electory.world.IChunkProvider;
import electory.world.IChunkSaveStatusHandler;
import electory.world.World;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class ChunkGenerator implements IChunkProvider {

	private World world;
	private long seed;

	public ChunkGenerator(World world, long seed) {
		this.world = world;
		this.seed = seed;
	}

	@Override
	public void populate(IChunkProvider provider, int x, int z) {

	}

	@Override
	public Chunk loadChunkSynchronously(int cx, int cy) {
		VoronoiGenerator gen = new VoronoiGenerator(seed ^ 0x1337L, 128);
		PerlinGenerator pgen = new PerlinGenerator(seed ^ 0xCAFEBABE, 8, 3, 0.5, false);
		PerlinGenerator rgen = new PerlinGenerator(seed ^ 0xDEADBEEF, 6, 2, 0.5, false);

		byte[] chunkData = new byte[65536];
		final byte[] biomeData = new byte[0x100];

		int[][] pArrMin = new int[31][31];
		int[][] pArrMax = new int[31][31];

		for (int i = 0; i < 31; i++) {
			for (int j = 0; j < 31; j++) {
				int ii = i - 8;
				int jj = j - 8;
				int x = (cx << 4) + ii;
				int y = (cy << 4) + jj;

				double val1 = pgen.generate(x / 256.0, y / 256.0);
				double val1c = val1;
				if (val1c > 1)
					val1c = 1;
				else if (val1c < 0)
					val1c = 0;
				double val = gen.generate(x, y, (int) (val1 * 256) - 128);
				int biomeId = (int) (val * EnumWorldBiome.biomeList.length);
				EnumWorldBiome biome = EnumWorldBiome.biomeList[biomeId];
				pArrMin[i][j] = biome.minHeight;//val < 0.5 ? 32 : 64;
				pArrMax[i][j] = biome.maxHeight;//val < 0.5 ? 48 : 72;

				// pArr[i][j] = (int) MathUtils.lerp(relief, minh, maxh);
				
				if(ii >= 0 && jj >= 0 && ii < 16 && jj < 16) {
					biomeData[jj << 4 | ii] = (byte) biomeId;
				}
			}
		}
		int[][] psumsMin = MathUtils.doPartialSums(pArrMin);
		int[][] psumsMax = MathUtils.doPartialSums(pArrMax);

		int sandId = world.blockIdRegistry.getBlockId(Block.REGISTRY.get("sand"));
		int grassId = world.blockIdRegistry.getBlockId(Block.REGISTRY.get("grass"));
		int dirtId = world.blockIdRegistry.getBlockId(Block.REGISTRY.get("dirt"));
		int waterId = world.blockIdRegistry.getBlockId(Block.REGISTRY.get("water"));
		int rootstoneId = world.blockIdRegistry.getBlockId(Block.REGISTRY.get("rootstone"));

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int x = (cx << 4) | i;
				int y = (cy << 4) | j;

				double relief = rgen.generate(x / 64.0, y / 64.0);
				if (relief < 0)
					System.out.println(relief);
				int minh = MathUtils.getRectangleSum(psumsMin, i, j, 16, 16) / 256;
				int maxh = MathUtils.getRectangleSum(psumsMax, i, j, 16, 16) / 256;
				int maxZ = (int) MathUtils.lerp(relief, minh, maxh);

				for (int z = 0; z < maxZ; z++) {
					if(maxZ < 64) {
						chunkData[i << 12 | j << 8 | (z & 0xFF)] = (byte) sandId;
					} else {
						chunkData[i << 12 | j << 8 | (z & 0xFF)] = (z == maxZ - 1) ? (byte) grassId : (byte) dirtId;
					}
					
					if(z == 0) {
						chunkData[i << 12 | j << 8 | (z & 0xFF)] = (byte) rootstoneId; 
					}
				}
				for (int z = maxZ; z < 64; z++) {
					chunkData[i << 12 | j << 8 | (z & 0xFF)] = (byte) waterId;
				}
			}
		}

		Chunk chunk = new Chunk(world, cx, cy);

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int z = 0; z < 256; z++) {
					chunk.setBlockAt(	i,
										z,
										j,
										world.blockIdRegistry.getBlockById(chunkData[i << 12 | j << 8 | (z & 0xFF)]),
										World.FLAG_SKIP_UPDATE);
				}
				chunk.setBiomeAt(i, j, EnumWorldBiome.biomeList[biomeData[j << 4 | i]]);
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
	public boolean canLoadChunk(int cx, int cy) {
		return true;
	}

	@Override
	public void waitUntilAllChunksSaved() {
	}

	@Override
	public void reset() {
	}

	@Override
	public Chunk provideChunk(int cx, int cy) {
		return null;
	}

	@Override
	public Collection<Chunk> getAllLoadedChunks() {
		return null;
	}

	@Override
	public boolean isChunkLoaded(int x, int z) {
		return true; // don't ask wtf
	}

	@Override
	public void unloadChunk(Chunk chunk, Iterator<Chunk> it, boolean doSave) {
	}

	@Override
	public void coldUnloadAllChunks() {
	}

	@Override
	public Long2ObjectOpenHashMap<Chunk> getLoadedChunkMap() {
		return null;
	}

	@Override
	public void loadChunk(int cx, int cy) {
		throw new UnsupportedOperationException();
	}
}
