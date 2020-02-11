package electory.item;

import electory.client.render.item.IItemRenderer;
import electory.utils.IRenderable;
import electory.utils.IUnit;

public class Item implements IUnit, IRenderable {
	
	public int itemID;
	public int itemSubID;
	public String anyName = "item.null.name";
	public int maxStackSize = 100;
	

	public Item() {
	}
	
	public IItemRenderer getRenderer() {
		return null;
	}
}
