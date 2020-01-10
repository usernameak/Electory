package electory.world.gen;

import java.util.Collection;
import java.util.Iterator;

import com.koloboke.collect.map.LongObjMap;

import electory.block.Block;
import electory.math.MathUtils;
import electory.world.BiomeGenBase;
import electory.world.Chunk;
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
	public void populate(IChunkProvider provider, int x, int z) {

	}

	@Override
	public Chunk loadChunk(int cx, int cy) {
		VoronoiGenerator gen = new VoronoiGenerator(seed ^ 0x1337L, 128);
		PerlinGenerator pgen = new PerlinGenerator(seed ^ 0xCAFEBABE, 8, 3, 0.5);
		PerlinGenerator rgen = new PerlinGenerator(seed ^ 0xDEADBEEF, 6, 2, 0.5);

		byte[] chunkData = new byte[32768];
		byte[] chunkDataExt = new byte[32768];
		final byte[] biomeData = new byte[0x100];
		
		int[][] pArr = new int[31][31];

		for (int i = 0; i < 31; i++) {
			for (int j = 0; j < 31; j++) {
				int ii = i - 8;
				int jj = j - 8;
				int x = (cx << 4) + ii;
				int y = (cy << 4) + jj;

				double val1 = pgen.generate(x / 64.0, y / 64.0);
				double val1c = val1;
				if (val1c > 1)
					val1c = 1;
				else if (val1c < 0)
					val1c = 0;
				double val = gen.generate(x, y, (int) (val1 * 256) - 128);
				pArr[i][j] = val < 0.5 ? 64 : 32;
			}
		}
		int[][] psums = MathUtils.doPartialSums(pArr);

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int x = (cx << 4) | i;
				int y = (cy << 4) | j;
				
/*
				for (int k = -8; k < 8; k++) {
					for (int l = -8; l < 8; l++) {
						double val1 = pgen.generate((x + k) / 64.0, (y + l) / 64.0);
						double val1c = val1;
						if (val1c > 1)
							val1c = 1;
						else if (val1c < 0)
							val1c = 0;
						double val = gen.generate(x + k, y + l, (int) (val1 * 256) - 128);
						totalBiomeHeightVal += val < 0.5 ? 64 : 32;
					}
				}*/
				
				int totalBiomeHeightVal = MathUtils.getRectangleSum(psums, i, j, 16, 16) / 256;

				double relief = rgen.generate(x / 64.0, y / 64.0);

				int heightOffset = (int) (relief * 16);

				int maxZ = (int) (heightOffset + totalBiomeHeightVal);

				for (int z = 0; z < maxZ; z++) {
					(z >= 128 ? chunkDataExt : chunkData)[i << 11
							| j << 7
							| (z & 0x7F)] = maxZ < 64 ? (byte) Block.blockSand.blockID : (byte) Block.blockGrass.blockID;
				}
				for(int z = maxZ; z < 64; z++) {
					(z >= 128 ? chunkDataExt : chunkData)[i << 11
					          							| j << 7
					          							| (z & 0x7F)] = (byte) Block.blockWater.blockID;
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
										Block.blockList[(z >= 128 ? chunkDataExt : chunkData)[i << 11
												| j << 7
												| (z & 0x7F)]],
										World.FLAG_SKIP_UPDATE);
				}
				chunk.setBiomeAt(i, j, BiomeGenBase.biomeList[biomeData[j << 4 | i]]);
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
	public LongObjMap<Chunk> getLoadedChunkMap() {
		return null;
	}
}
