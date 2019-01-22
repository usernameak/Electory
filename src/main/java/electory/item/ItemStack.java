package electory.item;

import electory.nbt.CompoundTag;
import electory.utils.IMetaSerializable;

public class ItemStack implements IMetaSerializable {

	public Item item;
	public int count;
	public Object meta;

	public ItemStack() {
		this(null);
	}

	public ItemStack(Item item) {
		this(item, 1);
	}

	public ItemStack(Item item, int count) {
		this(item, count, null);
	}

	public ItemStack(Item item, int count, Object meta) {
		this.item = item;
		this.count = count;
		this.meta = meta;
	}

	public boolean transfer(ItemStack other, int amount) {
		if(this.count < amount) {
			return false;
		}
		other.item = this.item;
		other.count = amount <= 0 ? this.count : amount;
		other.meta = this.meta;
		this.count = this.count - amount;
		return true;
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		// TODO:
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		// TODO Auto-generated method stub

	}

}
