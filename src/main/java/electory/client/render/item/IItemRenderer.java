package electory.client.render.item;

import electory.client.gui.GuiRenderState;
import electory.item.ItemStack;

public interface IItemRenderer {
	public static final IItemRenderer block = new RenderBlock();
	abstract public void render(ItemStack stack, GuiRenderState rs);
}
