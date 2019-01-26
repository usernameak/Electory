package electory.item;

import electory.block.Block;
import electory.client.render.item.IItemRenderer;

public class ItemBlock extends Item {

	public ItemBlock(int id) {
		super(id);
	}
	
	public Block getBlock() {
		return Block.blockList[itemID];
	}

	@Override
	public IItemRenderer getRenderer() {
		return IItemRenderer.block;
	}
}
