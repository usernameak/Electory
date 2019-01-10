package electory.world.gen.biome;

import electory.world.gen.condition.IntegerCondition;
import electory.world.gen.heightmap.IHeightMapGenerator;

public class BiomeConditionHeightMapFilter implements IHeightMapGenerator {
	private final IntegerCondition condition;
	private IHeightMapGenerator parent;
	private int conditionValue;
	private IBiomeMutator biomeTrue;
	private IBiomeMutator biomeFalse;

	public BiomeConditionHeightMapFilter(IHeightMapGenerator parent, IntegerCondition condition, int conditionValue,
			IBiomeMutator biomeTrue, IBiomeMutator biomeFalse) {
		super();
		this.condition = condition;
		this.parent = parent;
		this.conditionValue = conditionValue;
		this.biomeTrue = biomeTrue;
		this.biomeFalse = biomeFalse;
	}

	@Override
	public int[][] generateHeightmap(IBiomeAccessor biomeTrigger, int cx, int cy) {
		int[][] hmap1 = parent.generateHeightmap(biomeTrigger, cx, cy);

		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				if (condition.doComparsion(hmap1[x][y], conditionValue)) {
					if (biomeTrue != null) {
						biomeTrigger.setBiome(x, y, biomeTrue.mutate(biomeTrigger.getBiome(x, y), cx, cy, x, y));
					}
				} else if(biomeFalse != null) {
					biomeTrigger.setBiome(x, y, biomeFalse.mutate(biomeTrigger.getBiome(x, y), cx, cy, x, y));
				}
			}
		}

		return hmap1;
	}
}
