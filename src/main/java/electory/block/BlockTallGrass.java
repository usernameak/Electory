package electory.block;

import electory.client.render.block.IBlockRenderer;

public class BlockTallGrass extends Block {

	public BlockTallGrass(int id) {
		super(id);
	}
	
	@Override
	public IBlockRenderer getRenderer() {
		return IBlockRenderer.plant;
	}

	@Override
	public byte getSkyLightOpacity() {
		return 0;
	}
}
