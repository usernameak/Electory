package electory.world.gen.biome;

import electory.world.BiomeGenBase;

public interface IBiomeAccessor {
	public void setBiome(int x, int y, BiomeGenBase biome);
	public BiomeGenBase getBiome(int x, int y);
}
