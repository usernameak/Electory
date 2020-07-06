package electory.block;

import electory.client.render.block.IBlockRenderer;

public class BlockTallGrass extends Block {

	public BlockTallGrass() {
		super();
	}
	
	@Override
	public IBlockRenderer getRenderer() {
		return IBlockRenderer.plant;
	}

	@Override
	public byte getLightOpacity(int lightLevelType) {
		return 0;
	}
	
	@Override
	public boolean canBeReplaced() {
		return true;
	}
}
