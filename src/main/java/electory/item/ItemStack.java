package electory.item;

import electory.utils.IMetaSerializable;
import electory.utils.MetaSerializer;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

import java.io.IOException;
import java.util.Objects;

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
	public void writeToNBT(ArrayDataOutput tag) throws IOException {
		tag.writeUTF(item == null ? "" : item.getRegistryName());
		tag.writeInt(count | (meta == null ? 0 : 0x80000000));
		if(meta != null) {
			MetaSerializer.serializeObject(tag, meta);
		}
	}

	@Override
	public String toString() {
		return "ItemStack [item=" + item + ", count=" + count + ", meta=" + meta + "]";
	}

	@Override
	public void readFromNBT(ArrayDataInput tag) throws IOException {
		String name = tag.readUTF();
		item = name.isEmpty() ? null : Item.REGISTRY.get(name);
		if(item == null) {
			count = 0;
			meta = null;
			return;
		}
		count = tag.readInt();
		if ((count & 0x80000000) != 0) {
			meta = MetaSerializer.deserializeObject(tag, item.getMetadataClass());
		} else {
			meta = null;
		}
		count &= 0x7FFFFFFF;
	}

}
