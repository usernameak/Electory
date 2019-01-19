package electory.world.gen.biomemap;

import electory.world.BiomeGenBase;
import electory.world.gen.biome.IBiomeAccessor;

public interface IBiomeMapGenerator {
	int[][] generateBiomeMap(int cx, int cy);

	public static BiomeGenBase getBiomeA(int val) {
		return BiomeGenBase.biomeList[val & 0xFF];
	}

	public static BiomeGenBase getBiomeB(int val) {
		return BiomeGenBase.biomeList[(val >> 8) & 0xFF];
	}

	public static float getBiomeInterpolationValue(int val) {
		return ((val >> 16) & 0xFF) / 255.f;
	}

	public static BiomeGenBase getPreferredBiome(int val) {
		return getPreferredBiome(getBiomeA(val), getBiomeB(val), getBiomeInterpolationValue(val));
	}

	public static BiomeGenBase getPreferredBiome(BiomeGenBase biomeA, BiomeGenBase biomeB,
			float biomeInterpolationValue) {
		return biomeInterpolationValue >= 0.5f ? biomeB : biomeA;
	}

	public static void applyBiomes(int[][] map, IBiomeAccessor accessor) {

		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				accessor.setBiome(x, y, getPreferredBiome(map[x][y]));
			}
		}
	}
}
