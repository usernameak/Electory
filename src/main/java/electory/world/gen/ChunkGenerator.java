package electory.world.gen;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import electory.block.Block;
import electory.math.MathUtils;
import electory.world.BiomeGenBase;
import electory.world.Chunk;
import electory.world.ChunkPosition;
import electory.world.IChunkProvider;
import electory.world.IChunkSaveStatusHandler;
import electory.world.World;
import electory.world.gen.biome.BiomeConditionHeightMapFilter;
import electory.world.gen.biome.DummyBiomeAccessor;
import electory.world.gen.biome.IBiomeAccessor;
import electory.world.gen.biome.IBiomeMutator;
import electory.world.gen.condition.IntegerCondition;
import electory.world.gen.heightmap.BasicHeightMapGenerator;
import electory.world.gen.heightmap.ConditionalAddHeightMapGenerator;
import electory.world.gen.heightmap.IHeightMapGenerator;
import electory.world.gen.heightmap.Lerp4HeightMapGenerator;
import electory.world.gen.heightmap.OverScanHeightMapGenerator;
import electory.world.gen.heightmap.postprocessor.Lerp4HeightMapPostProcessor;
import electory.world.gen.heightmap.postprocessor.RangeRemapPostProcessor;
import electory.world.gen.noise.DSNoise;
import electory.world.gen.noise.FBM;
import electory.world.gen.noise.ScaledNoise;

public class ChunkGenerator implements IChunkProvider {

	private World world;
	private IHeightMapGenerator heightMapGen;
	private IHeightMapGenerator forestnessGen;
	private MapGenCaves caveGenerator;
	private Random rand = new Random();

	public ChunkGenerator(World world, long seed) {
		this.world = world;
		/*
		 * this.heightMapGen = new ConditionalAddHeightMapGenerator( new
		 * OverScanHeightMapGenerator(new DSNoise(60000000, 60000000 / 100.f, seed), 64,
		 * 72, 64, 72, 17, 1), new Lerp4HeightMapGenerator(new DSNoise(60000000,
		 * 60000000 / 400.f, seed ^ 0xFADEC0FFEE101F00l), 0, 1024, 0, 1024),
		 * IntegerCondition.GREATER, 512, new RangeRemapPostProcessor(512.f, 1024.f,
		 * 0.f, 183.f), BiomeGenBase.plains, BiomeGenBase.extremeHills, new
		 * Lerp4HeightMapPostProcessor());
		 */
		this.heightMapGen = new ConditionalAddHeightMapGenerator(
				new OverScanHeightMapGenerator(new ScaledNoise(new FBM(8, seed), 1f / 32, 1f / 32), 64, 72, 64, 72, 17, 1),
				new Lerp4HeightMapGenerator(new ScaledNoise(new FBM(8, seed ^ 0xFADEC0FFEE101F00l), 1f / 128, 1f / 128), 0,
						1024, 0, 1024),
				IntegerCondition.GREATER, 512, new RangeRemapPostProcessor(512.f, 1024.f, 0.f, 183.f),
				BiomeGenBase.plains, BiomeGenBase.extremeHills, new Lerp4HeightMapPostProcessor());
		/*new DSNoise(60000000, 60000000 / 200.f, seed ^ 0x793379EE79E3793El)*/
		this.forestnessGen = new BiomeConditionHeightMapFilter(
				new BasicHeightMapGenerator(new ScaledNoise(new FBM(8, seed ^ 0x793379EE79E3793El), 1f / 64, 1f / 64), -100,
						100, -100, 100),
				IntegerCondition.GREATER_OR_EQUAL, 0, new IBiomeMutator() {
					@Override
					public BiomeGenBase mutate(BiomeGenBase oldBiome, int chunkX, int chunkZ, int x, int z) {
						if (oldBiome.equals(BiomeGenBase.plains)) {
							return BiomeGenBase.forest;
						} else if (oldBiome.equals(BiomeGenBase.extremeHills)
								|| oldBiome.equals(BiomeGenBase.extremeHillsEdge)) {
							return BiomeGenBase.forestHills;
						} else {
							return oldBiome;
						}
					}
				}, null);
		this.caveGenerator = new MapGenCaves(new Random(seed ^ 0xA773F00CCA8E9E10l));
	}

	@Override
	public void populate(IChunkProvider provider, int x, int z) {
		rand.setSeed(x ^ (z << 16) ^ world.seed ^ 0x17l);

		for (int i = 0; i < 32; i++) {
			int wx = (x << 4) + rand.nextInt(16);
			int wz = (z << 4) + rand.nextInt(16);
			int y = world.getHeightAt(wx, wz);
			if (world.getBlockAt(wx, y, wz) == Block.blockGrass) {
				world.setBlockAt(wx, y + 1, wz, Block.blockTallGrass);
			}
		}

		rand.setSeed(x ^ (z << 16) ^ world.seed ^ 0x36l);

		int forestness = forestnessGen.generateHeightmap(new DummyBiomeAccessor(), x, z)[0][0];
		float forestnessF = MathUtils.rangeRemap(forestness, -100.f, 100.f, 0.0f, 10.0f);
		if (forestnessF >= 1 || this.rand.nextFloat() <= forestnessF) {
			int forestnessI = (int) (forestnessF >= 1 ? forestnessF : 1);

			for (int i = 0; i < forestnessI; i++) {
				int wx = (x << 4) + 8 + rand.nextInt(16);
				int wz = (z << 4) + 8 + rand.nextInt(16);

				for (int y = 255; y >= 0; y--) {
					Block block = world.getBlockAt(wx, y, wz);
					if (block == Block.blockDirt || block == Block.blockGrass) {
						for (int by = y + 1; by < y + 7; by++) {
							if (by >= y + 3) {
								int radius = by >= y + 6 ? 1 : 2;
								for (int bx = wx - radius; bx <= wx + radius; bx++) {
									for (int bz = wz - radius; bz <= wz + radius; bz++) {
										if (radius == 1 || !(bx == wx && bz == wz)) {
											world.setBlockAt(bx, by, bz, Block.blockLeaves);
										}
									}
								}
							}
							if (by < y + 6) {
								world.setBlockAt(wx, by, wz, Block.blockLog);
							}
						}
						break;
					} else if (block != null && !block.canBeReplaced()) {
						break;
					}
				}
			}
		}
	}

	@Override
	public Chunk loadChunk(int cx, int cy) {
		byte[] chunkData = new byte[32768];
		byte[] chunkDataExt = new byte[32768];
		final byte[] biomeData = new byte[0x100];

		final IBiomeAccessor biomeAccessor = new IBiomeAccessor() {

			@Override
			public void setBiome(int x, int y, BiomeGenBase biome) {
				biomeData[y << 4 | x] = (byte) biome.biomeID;
			}

			@Override
			public BiomeGenBase getBiome(int x, int y) {
				return BiomeGenBase.biomeList[biomeData[y << 4 | x]];
			}
		};

		int[][] heightData = heightMapGen.generateHeightmap(biomeAccessor, cx, cy);
		forestnessGen.generateHeightmap(biomeAccessor, cx, cy);

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int ih = heightData[i][j];

				BiomeGenBase biome = BiomeGenBase.biomeList[biomeData[j << 4 | i]];

				for (int z = 0; z < ih; z++) {
					Block block = Block.blockStone;
					if (z == 0) {
						block = Block.blockRootStone;
					} else if (z == ih - 1) {
						block = biome.topBlock;
					} else if (z > ih - 6) {
						block = biome.fillerBlock;
					}
					(z >= 128 ? chunkDataExt : chunkData)[i << 11 | j << 7 | (z & 0x7F)] = (byte) block.blockID;
				}
			}
		}

		caveGenerator.generate(this, world, cx, cy, chunkData);

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
	public Map<ChunkPosition, Chunk> getLoadedChunkMap() {
		return null;
	}
}
