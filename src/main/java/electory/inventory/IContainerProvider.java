package electory.inventory;

import electory.entity.EntityPlayer;
import electory.item.ItemStack;

public interface IContainerProvider {
	void slotClicked(EntityPlayer player, int slot, SlotClickAction action);
	
	ItemStack getItemInSlot(int slot);
}
