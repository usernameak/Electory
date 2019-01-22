package electory.world.gen;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import com.koloboke.collect.map.LongObjMap;

import electory.block.Block;
import electory.world.BiomeGenBase;
import electory.world.Chunk;
import electory.world.IChunkProvider;
import electory.world.IChunkSaveStatusHandler;
import electory.world.World;
import electory.world.gen.biome.IBiomeAccessor;
import electory.world.gen.condition.IntegerCondition;
import electory.world.gen.feature.WorldGenFeatureTree;
import electory.world.gen.heightmap.ConditionalAddHeightMapGenerator;
import electory.world.gen.heightmap.IHeightMapGenerator;
import electory.world.gen.heightmap.Lerp4HeightMapGenerator;
import electory.world.gen.heightmap.OverScanHeightMapGenerator;
import electory.world.gen.heightmap.postprocessor.Lerp4HeightMapPostProcessor;
import electory.world.gen.heightmap.postprocessor.RangeRemapPostProcessor;
import electory.world.gen.noise.FBM;
import electory.world.gen.noise.PoweredNoise;
import electory.world.gen.noise.ScaledNoise;

public class ChunkGenerator implements IChunkProvider {

	private World world;
	/*
	 * private IHeightMapGenerator heightMapGens[]; private IBiomeMapGenerator
	 * biomeMapGen;
	 */
	private IHeightMapGenerator heightMapGen;
	// private IHeightMapGenerator forestnessGen;
	private MapGenCaves caveGenerator;
	private Random rand = new Random();
	private WorldGenFeatureTree treeGenerator = new WorldGenFeatureTree();

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
		/*
		 * this.heightMapGen = new ConditionalAddHeightMapGenerator( new
		 * OverScanHeightMapGenerator(new ScaledNoise(new FBM(8, seed), 1f / 32, 1f /
		 * 32), 64, 72, 64, 72, 17, 1), new Lerp4HeightMapGenerator(new ScaledNoise(new
		 * FBM(8, seed ^ 0xFADEC0FFEE101F00l), 1f / 128, 1f / 128), 0, 1024, 0, 1024),
		 * IntegerCondition.GREATER, 512, new RangeRemapPostProcessor(512.f, 1024.f,
		 * 0.f, 183.f), BiomeGenBase.plains, BiomeGenBase.extremeHills, new
		 * Lerp4HeightMapPostProcessor());
		 */
		/*
		 * this.heightMapGens = new IHeightMapGenerator[BiomeGenBase.biomeList.length];
		 * for (int i = 0; i < BiomeGenBase.biomeList.length; i++) { if
		 * (BiomeGenBase.biomeList[i] != null) { heightMapGens[i] =
		 * BiomeGenBase.biomeList[i].createHeightMapGenerator(seed); } }
		 */

		heightMapGen = new ConditionalAddHeightMapGenerator(
				new OverScanHeightMapGenerator(
						new PoweredNoise(new ScaledNoise(new FBM(8, seed), 1f / 256, 1f / 256), 0.66666f), 32, 72, 32,
						72, 17, 1),
				new Lerp4HeightMapGenerator(new PoweredNoise(
						new ScaledNoise(new FBM(8, seed ^ 0xDEADBEEFCAFEBABEl), 1f / 256, 1f / 256), 1.66f), 0, 1024, 0,
						1024),
				IntegerCondition.GREATER, 512, new RangeRemapPostProcessor(512.f, 1024.f, 0.f, 183.f), null,
				BiomeGenBase.extremeHills, new Lerp4HeightMapPostProcessor());

		/*
		 * heightMapGen = new BasicHeightMapGenerator( new PoweredNoise(new
		 * ScaledNoise(new FBM(8, seed), 1f / 256, 1f / 256), 0.66666f), 32, 72, 32,
		 * 72);
		 */
		// = new BasicHeightMapGenerator(new ScaledNoise(new FBM(8, seed), 1f / 32, 1f /
		// 32), 64, 72, 64, 72);
		/*
		 * this.biomeMapGen = new FuzzyChunkBlurBiomeMapGenerator(new
		 * VoronoiBiomeMapGenerator(seed ^ 0xDEADBEEFCAFEBABEL, BiomeGenBase.plains,
		 * BiomeGenBase.ocean));
		 */
		/* new DSNoise(60000000, 60000000 / 200.f, seed ^ 0x793379EE79E3793El) */
		/*
		 * this.forestnessGen = new BiomeConditionHeightMapFilter( new
		 * BasicHeightMapGenerator(new ScaledNoise(new FBM(8, seed ^
		 * 0x793379EE79E3793El), 1f / 64, 1f / 64), -100, 100, -100, 100),
		 * IntegerCondition.GREATER_OR_EQUAL, 0, new IBiomeMutator() {
		 * 
		 * @Override public BiomeGenBase mutate(BiomeGenBase oldBiome, int chunkX, int
		 * chunkZ, int x, int z) { if (oldBiome.equals(BiomeGenBase.plains)) { return
		 * BiomeGenBase.forest; } else if (oldBiome.equals(BiomeGenBase.extremeHills) ||
		 * oldBiome.equals(BiomeGenBase.extremeHillsEdge)) { return
		 * BiomeGenBase.forestHills; } else { return oldBiome; } } }, null);
		 */
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

		for (int i = 0; i < 4; i++) {
			int wx = (x << 4) + 8 + rand.nextInt(16);
			int wz = (z << 4) + 8 + rand.nextInt(16);

			for (int y = 255; y >= 0; y--) {
				Block block = world.getBlockAt(wx, y, wz);
				if (block == Block.blockDirt || block == Block.blockGrass) {
					world.setBlockAt(wx, y + 1, wz, null);
					treeGenerator.generate(world, wx, y + 1, wz, rand);
					break;
				} else if (block != null && !block.canBeReplaced()) {
					break;
				} else {
				}
			}
		}
	}

	/*private int lerp(int a, int b, float i) {
		return (int) (a + i * (b - a));
	}*/

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

		/*
		 * int[][] biomeMap = biomeMapGen.generateBiomeMap(cx, cy);
		 * 
		 * IBiomeMapGenerator.applyBiomes(biomeMap, biomeAccessor);
		 */

		int[][] heightData = heightMapGen.generateHeightmap(biomeAccessor, cx, cy);
		/*
		 * int[][][] heightDatas = new int[heightMapGens.length][][]; //
		 * forestnessGen.generateHeightmap(biomeAccessor, cx, cy);
		 */

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				/*
				 * int bid1 = IBiomeMapGenerator.getBiomeA(biomeMap[i][j]).biomeID; int bid2 =
				 * IBiomeMapGenerator.getBiomeB(biomeMap[i][j]).biomeID; // int bid =
				 * biomeAccessor.getBiome(i, j).biomeID; if (heightDatas[bid1] == null) {
				 * heightDatas[bid1] = heightMapGens[bid1].generateHeightmap(biomeAccessor, cx,
				 * cy); } if (heightDatas[bid2] == null) { heightDatas[bid2] =
				 * heightMapGens[bid2].generateHeightmap(biomeAccessor, cx, cy); }
				 * 
				 * int ih = lerp(heightDatas[bid1][i][j], heightDatas[bid2][i][j],
				 * IBiomeMapGenerator.getBiomeInterpolationValue(biomeMap[i][j]) / 255.0f);
				 */

				int ih = heightData[i][j];

				if (ih < 64) {
					biomeData[j << 4 | i] = (byte) BiomeGenBase.ocean.biomeID;
				}

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

				for (int z = ih; z < 64; z++) {
					(z >= 128 ? chunkDataExt : chunkData)[i << 11
							| j << 7
							| (z & 0x7F)] = (byte) Block.blockWater.blockID;
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
	public LongObjMap<Chunk> getLoadedChunkMap() {
		return null;
	}
}
