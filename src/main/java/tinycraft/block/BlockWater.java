package tinycraft.block;

import tinycraft.client.render.world.WorldRenderer;

public class BlockWater extends Block {

	public BlockWater(int id) {
		super(id);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == WorldRenderer.RENDERPASS_LIQUID1;
	}
}
