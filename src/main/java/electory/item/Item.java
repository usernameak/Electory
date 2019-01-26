package electory.item;

import electory.client.render.item.IItemRenderer;

public class Item {
	public final int itemID;
	
	public static Item itemList[] = new Item[32768];

	public Item(int id) {
		itemList[id] = this;
		this.itemID = id;
	}
	
	public IItemRenderer getRenderer() {
		return null;
	}

}
