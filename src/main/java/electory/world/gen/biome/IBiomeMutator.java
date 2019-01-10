package electory.world.gen.biome;

import electory.world.BiomeGenBase;

public interface IBiomeMutator {
	public BiomeGenBase mutate(BiomeGenBase oldBiome, int chunkX, int chunkZ, int x, int z);
}
