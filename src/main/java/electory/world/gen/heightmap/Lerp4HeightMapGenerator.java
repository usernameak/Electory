package electory.world.gen.heightmap;

import electory.world.gen.biome.IBiomeAccessor;
import electory.world.gen.noise.IWorldNoiseGenerator;

public class Lerp4HeightMapGenerator extends OverScanHeightMapGenerator {
	public Lerp4HeightMapGenerator(IWorldNoiseGenerator noise, int clampMin, int clampMax, int interpolMin,
			int interpolMax) {
		super(noise, clampMin, clampMax, interpolMin, interpolMax, 17, 4);
	}

	@Override
	public int[][] generateHeightmap(IBiomeAccessor biomeTrigger, int cx, int cy) {
		int[][] heightData = super.generateHeightmap(biomeTrigger, cx, cy);
		
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				float tx = ((i) - (i - i % 4)) / 4.f;
				float ty = ((j) - (j - j % 4)) / 4.f;
				heightData[i][j] = Math.round(blerp(heightData[i - i % 4][j - j % 4], heightData[i - i % 4 + 4][j - j % 4],
						heightData[i - i % 4][j - j % 4 + 4], heightData[i - i % 4 + 4][j - j % 4 + 4], tx, ty));
			}
		}

		return heightData;
	}
}
