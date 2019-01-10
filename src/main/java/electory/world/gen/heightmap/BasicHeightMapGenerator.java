package electory.world.gen.heightmap;

import electory.world.gen.biome.IBiomeAccessor;
import electory.world.gen.noise.IWorldNoiseGenerator;

public class BasicHeightMapGenerator implements IHeightMapGenerator {
	private int clampMin;
	private int clampMax;
	private int interpolMin;
	private int interpolMax;
	private IWorldNoiseGenerator noise;

	public BasicHeightMapGenerator(IWorldNoiseGenerator noise, int clampMin, int clampMax, int interpolMin, int interpolMax) {
		this.clampMin = clampMin;
		this.clampMax = clampMax;
		this.interpolMin = interpolMin;
		this.interpolMax = interpolMax;
		this.noise = noise;
	}
	
	protected int heightAt(int x, int y) {
		int xof = x + 30000000;
		int yof = y + 30000000;

		double h = noise.val(xof, yof);
		int ih = ilerp(interpolMin, interpolMax, h);
		if (ih > clampMax)
			ih = clampMax;
		if (ih < clampMin)
			ih = clampMin;

		return ih;
	}	
	
	protected int ilerp(int s, int e, double t) {
		return (int) (s + (e - s) * t);
	}
	
	protected float lerp(float s, float e, float t) {
		return s + (e - s) * t;
	}

	protected float blerp(float c00, float c10, float c01, float c11, float tx, float ty) {
		return lerp(lerp(c00, c10, tx), lerp(c01, c11, tx), ty);
	}

	public int[][] generateHeightmap(IBiomeAccessor biomeTrigger, int cx, int cy) {
		int[][] heightData = new int[16][16];

		for (int i = 0; i < 16; i ++) {
			for (int j = 0; j < 16; j ++) {
				int wx = cx * 16 + i;
				int wy = cy * 16 + j;

				heightData[i][j] = Math.round(heightAt(wx, wy));
			}
		}
		
		return heightData;
	}
}
