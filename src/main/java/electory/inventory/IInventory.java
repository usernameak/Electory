package electory.inventory;

import electory.item.ItemStack;

public interface IInventory {
	void setStackInSlot(int slot, ItemStack stack);

	ItemStack getStackInSlot(int slot);
}
