package electory.inventory;

import electory.entity.EntityPlayer;
import electory.item.ItemStack;

public interface IContainerProvider {
	void pickupItemFromSlot(EntityPlayer player, int slot);
	
	void putItemToSlot(EntityPlayer player, int slot);
	
	ItemStack getItemInSlot(int slot);
}
