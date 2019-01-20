package electory.world.gen.feature;

import java.util.Random;

import electory.world.World;

public interface IWorldGenFeature {
	void generate(World world, int x, int y, int z, Random random);
}
