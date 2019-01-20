package electory.world.gen.feature;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import electory.block.Block;
import electory.utils.EnumSide;
import electory.world.World;

public class WorldGenFeatureTree implements IWorldGenFeature {
	public void generate(World world, int x, int y, int z, Random random) {
		recursiveGenerate(world, x, y, z, EnumSide.UP, 0, random);
	}

	public boolean recursiveGenerate(World world, int x, int y, int z, EnumSide direction, int level, Random random) {
		if (world.getBlockAt(x, y, z) != null && world.getBlockAt(x, y, z) != Block.blockLeaves) {
			return false;
		}

		if (level < 4) {
			world.setBlockWithMetadataAt(x, y, z, Block.blockLog, direction.axis, 0);
			world.setBlockAt(x, y, z, Block.blockLog);
			recursiveGenerate(	world,
								x + direction.offsetX,
								y + direction.offsetY,
								z + direction.offsetZ,
								direction,
								level + 1,
								random);
			return true;
		} else if (level < 12) {
			world.setBlockWithMetadataAt(x, y, z, Block.blockLog, direction.axis, 0);
			world.setBlockAt(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, Block.blockLeaves);
			EnumSide[] sides;
			if (direction == EnumSide.UP) {
				sides = new EnumSide[] { EnumSide.EAST, EnumSide.WEST, EnumSide.SOUTH, EnumSide.NORTH, direction };
			} else if (direction == EnumSide.WEST || direction == EnumSide.EAST) {
				sides = new EnumSide[] { EnumSide.UP, EnumSide.DOWN, EnumSide.SOUTH, EnumSide.NORTH, direction };
			} else {
				sides = new EnumSide[] { EnumSide.UP, EnumSide.DOWN, EnumSide.EAST, EnumSide.WEST, direction };
			}

			for (EnumSide side : sides) {
				if (world.getBlockAt(x + side.offsetX, y + side.offsetY, z + side.offsetZ) == Block.blockLog) {
					return false;
				}
				world.setBlockAt(x + side.offsetX, y + side.offsetY, z + side.offsetZ, Block.blockLeaves);
			}

			// int r = world.random.nextInt(2) + 1;

			List<EnumSide> newSides = new LinkedList<>(
					Arrays.asList(EnumSide.UP, EnumSide.WEST, EnumSide.EAST, EnumSide.SOUTH, EnumSide.NORTH));

			for (int i = 0; i < 5; i++) {
				if (world.random.nextInt((level - 2) / 2) < 1) {
					int idx = world.random.nextInt(newSides.size());
					direction = newSides.get(idx);
					newSides.remove(idx);
					if (direction == EnumSide.DOWN) {
						direction = EnumSide.UP;
					}
				}
				recursiveGenerate(	world,
									x + direction.offsetX,
									y + direction.offsetY,
									z + direction.offsetZ,
									direction,
									level + 1,
									random);
			}
		}

		return true;
	}
}
