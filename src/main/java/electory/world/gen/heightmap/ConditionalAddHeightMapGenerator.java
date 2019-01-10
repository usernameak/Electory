package electory.world.gen.heightmap;

import java.util.Arrays;

import electory.world.BiomeGenBase;
import electory.world.gen.biome.IBiomeAccessor;
import electory.world.gen.condition.IntegerCondition;
import electory.world.gen.heightmap.postprocessor.IHeightMapPostProcessor;

public class ConditionalAddHeightMapGenerator implements IHeightMapGenerator {
	private final IntegerCondition condition;
	private IHeightMapGenerator parent1;
	private int conditionValue;
	private IHeightMapGenerator parent2;
	private IHeightMapPostProcessor hmap2_pp;
	private BiomeGenBase biome1;
	private BiomeGenBase biome2;
	private IHeightMapPostProcessor res2_pp;

	public ConditionalAddHeightMapGenerator(IHeightMapGenerator parent1, IHeightMapGenerator parent2,
			IntegerCondition condition, int conditionValue, IHeightMapPostProcessor hmap2_pp, BiomeGenBase biome1,
			BiomeGenBase biome2, IHeightMapPostProcessor res2_pp) {
		super();
		this.condition = condition;
		this.parent1 = parent1;
		this.parent2 = parent2;
		this.conditionValue = conditionValue;
		this.hmap2_pp = hmap2_pp;
		this.biome1 = biome1;
		this.biome2 = biome2;
		this.res2_pp = res2_pp;
	}

	private static int[][] deepCopy(int[][] original) {
		if (original == null) {
			return null;
		}

		final int[][] result = new int[original.length][];
		for (int i = 0; i < original.length; i++) {
			result[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return result;
	}

	@Override
	public int[][] generateHeightmap(IBiomeAccessor biomeTrigger, int cx, int cy) {
		int[][] hmap1 = parent1.generateHeightmap(biomeTrigger, cx, cy);
		int[][] hmap2 = parent2.generateHeightmap(biomeTrigger, cx, cy);
		int[][] hmap3 = hmap2;
		int[][] hmap4 = deepCopy(hmap1);
		if (hmap2_pp != null) {
			hmap3 = deepCopy(hmap3);
			hmap2_pp.postprocess(hmap3);
		}

		for (int x = 0; x < hmap4.length; x++) {
			for (int y = 0; y < hmap4[x].length; y++) {
				hmap4[x][y] += hmap3[x][y];
			}
		}
		if(res2_pp != null) {
			hmap4 = res2_pp.postprocess(hmap4);
		}
		
		for (int x = 0; x < hmap1.length; x++) {
			for (int y = 0; y < hmap1[x].length; y++) {
				if (condition.doComparsion(hmap2[x][y], conditionValue)) {
					hmap1[x][y] = hmap4[x][y];
					if (x < 16 && y < 16 && biome2 != null) {
						biomeTrigger.setBiome(x, y, biome2);
					}
				} else if (x < 16 && y < 16 && biome1 != null) {
					biomeTrigger.setBiome(x, y, biome1);
				}
			}
		}

		return hmap1;
	}

}
