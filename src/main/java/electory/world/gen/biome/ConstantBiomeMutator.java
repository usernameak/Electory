package electory.world.gen.biome;

import electory.world.BiomeGenBase;

public class ConstantBiomeMutator implements IBiomeMutator {
	private final BiomeGenBase biome;

	public ConstantBiomeMutator(BiomeGenBase biome) {
		super();
		this.biome = biome;
	}

	@Override
	public BiomeGenBase mutate(BiomeGenBase oldBiome, int chunkX, int chunkZ, int x, int z) {
		return biome;
	}
}
