package electory.block;

import electory.client.render.block.IBlockRenderer;
import electory.client.render.world.WorldRenderer;

public class BlockWater extends Block {

	public BlockWater(int id) {
		super(id);
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
