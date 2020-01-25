package electory.item;

import java.util.Objects;

import electory.nbt.CompoundTag;
import electory.utils.IMetaSerializable;
import electory.utils.MetaSerializer;

public class ItemStack implements IMetaSerializable {

	public Item item;
	public int count;
	public Object meta;

	public ItemStack() {
		this(null, 0);
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
		if (this.count < amount
				|| (other.count > 0
						&& ((other.item != null && other.item != this.item)
								|| !Objects.equals(other.meta, this.meta)))) {
			return false;
		}
		other.item = this.item;
		other.count += amount <= 0 ? this.count : amount;
		other.meta = this.meta;
		if (amount <= 0) {
			this.count = 0;
		} else {
			this.count = this.count - amount;
		}
		return true;
	}

	public boolean remove(int amount) {
		if (this.count < amount) {
			return false;
		}
		this.count -= amount;
		return true;
	}

	public boolean isEmpty() {
		return item == null || count <= 0;
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		tag.putString("item", item == null ? "" : item.getRegistryName());
		tag.putInt("count", count);
		if (meta != null) {
			tag.put("meta", MetaSerializer.serializeObject(meta));
		}
	}

	@Override
	public String toString() {
		return "ItemStack [item=" + item + ", count=" + count + ", meta=" + meta + "]";
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		String name = tag.getString("item");
		item = name.isEmpty() ? null : Item.REGISTRY.get(name);
		count = tag.getInt("count");
		if (tag.containsKey("meta")) {
			meta = MetaSerializer.deserializeObject(tag.getCompoundTag("meta"));
		} else {
			meta = null;
		}
	}

}
