package electory.block;

import electory.client.render.block.IBlockRenderer;
import electory.entity.EntityPlayer;
import electory.utils.EnumSide;
import electory.world.World;
import electory.world.gen.feature.WorldGenFeatureTree;

public class BlockSapling extends Block {

	public BlockSapling(int id) {
		super(id);
	}

	@Override
	public IBlockRenderer getRenderer() {
		return IBlockRenderer.plant;
	}

	@Override
	public boolean interactWithBlock(EntityPlayer player, World world, int x, int y, int z, EnumSide side) {
		WorldGenFeatureTree feature = new WorldGenFeatureTree();

		world.setBlockAt(x, y, z, null);

		feature.generate(world, x, y, z, world.random);

		return true;
	}
}
