package electory.block;

import electory.client.render.block.IBlockRenderer;

public class BlockSapling extends Block {

	public BlockSapling() {
		super();
	}

	@Override
	public IBlockRenderer getRenderer() {
		return IBlockRenderer.plant;
	}

	/*@Override
	public boolean interactWithBlock(EntityPlayer player, World world, int x, int y, int z, EnumSide side) {
		WorldGenFeatureTree feature = new WorldGenFeatureTree();

		world.setBlockAt(x, y, z, null);

		feature.generate(world, x, y, z, world.random);

		return true;
	}*/
}
