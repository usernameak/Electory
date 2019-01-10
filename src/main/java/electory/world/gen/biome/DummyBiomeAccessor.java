package electory.world.gen.biome;

import electory.world.BiomeGenBase;

public class DummyBiomeAccessor implements IBiomeAccessor {

	@Override
	public void setBiome(int x, int y, BiomeGenBase biome) {
	}

	@Override
	public BiomeGenBase getBiome(int x, int y) {
		return BiomeGenBase.plains;
	}

}
