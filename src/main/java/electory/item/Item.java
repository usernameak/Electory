package electory.item;

public class Item {
	public final int itemID;
	
	public static Item itemList[] = new Item[32768];

	public Item(int id) {
		itemList[id] = this;
		this.itemID = id;
	}

}
