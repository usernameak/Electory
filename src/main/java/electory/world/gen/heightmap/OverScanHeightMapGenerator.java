package electory.world.gen.heightmap;

import electory.world.gen.biome.IBiomeAccessor;
import electory.world.gen.noise.IWorldNoiseGenerator;

public class OverScanHeightMapGenerator extends BasicHeightMapGenerator {

	private int overScanAmount;
	private int step;

	public OverScanHeightMapGenerator(IWorldNoiseGenerator noise, int clampMin, int clampMax, int interpolMin,
			int interpolMax, int overScanAmount, int step) {
		super(noise, clampMin, clampMax, interpolMin, interpolMax);
		this.overScanAmount = overScanAmount;
		this.step = step;
	}

	@Override
	public int[][] generateHeightmap(IBiomeAccessor biomeTrigger, int cx, int cy) {
		int[][] heightData = new int[overScanAmount][overScanAmount];

		for (int i = 0; i < overScanAmount; i += step) {
			for (int j = 0; j < overScanAmount; j += step) {
				int wx = cx * 16 + i;
				int wy = cy * 16 + j;

				heightData[i][j] = heightAt(wx, wy);
			}
		}

		return heightData;
	}
}
