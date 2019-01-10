package electory.world.gen.heightmap;

import electory.world.gen.biome.IBiomeAccessor;
import electory.world.gen.noise.IWorldNoiseGenerator;

public class UnderScanHeightMapGenerator extends BasicHeightMapGenerator {

	private int underScanAmount;
	private int step;

	public UnderScanHeightMapGenerator(IWorldNoiseGenerator noise, int clampMin, int clampMax, int interpolMin,
			int interpolMax, int underScanAmount, int step) {
		super(noise, clampMin, clampMax, interpolMin, interpolMax);
		this.underScanAmount = underScanAmount;
		this.step = step;
	}

	@Override
	public int[][] generateHeightmap(IBiomeAccessor biomeTrigger, int cx, int cy) {
		int[][] heightData = new int[underScanAmount][underScanAmount];

		for (int i = 0, k = 0; i < underScanAmount && k < 16; i++, k += step) {
			for (int j = 0, l = 0; j < underScanAmount && l < 16; j++, l += step) {
				int wx = cx * 16 + k;
				int wy = cy * 16 + l;

				heightData[i][j] = heightAt(wx, wy);
			}
		}

		return heightData;
	}
}
