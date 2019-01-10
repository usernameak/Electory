package electory.world.gen.heightmap;

import electory.world.gen.biome.IBiomeAccessor;

public interface IHeightMapGenerator {
	int[][] generateHeightmap(IBiomeAccessor biomeTrigger, int cx, int cy);
}
