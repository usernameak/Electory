package electory.item;

import electory.block.Block;
import electory.client.render.item.IItemRenderer;

public class ItemBlock extends Item {

	public ItemBlock(Block block) {
		super();
	}
	
	public Block getBlock() {
		return Block.REGISTRY.get(getRegistryName());
	}

	@Override
	public IItemRenderer getRenderer() {
		return IItemRenderer.block;
	}
}
