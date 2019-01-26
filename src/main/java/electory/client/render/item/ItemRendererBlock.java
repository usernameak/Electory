package electory.client.render.item;

import electory.client.gui.GuiRenderState;
import electory.item.ItemBlock;
import electory.item.ItemStack;

public class ItemRendererBlock implements IItemRenderer {

	@Override
	public void render(ItemStack stack, GuiRenderState rs) {
		ItemBlock itemBlock = (ItemBlock) stack.item;
		
		itemBlock.getBlock().getRenderer().renderBlockInGUI(itemBlock.getBlock(), rs);
	}
	
}
