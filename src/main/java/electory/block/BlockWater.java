package electory.block;

import electory.client.render.block.IBlockRenderer;
import electory.client.render.world.WorldRenderer;

public class BlockWater extends Block {
	public BlockWater(int par1) {
		super(par1);
	}

	public byte getSkyLightOpacity() {
		return 1;
	}
	
	@Override
	public IBlockRenderer getRenderer() {
		return IBlockRenderer.cube;
	}
	
	@Override
	public boolean shouldRenderInVBO(int pass) {
		return pass == WorldRenderer.VBO_LIQUID1;
	}
}
