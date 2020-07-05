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
}
