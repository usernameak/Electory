package electory.inventory;

import electory.entity.EntityPlayer;
import electory.item.ItemStack;

public class ContainerProviderSP implements IContainerProvider {

	private IInventory inv;

	public ContainerProviderSP(IInventory inv) {
		this.inv = inv;
	}

	@Override
	public ItemStack getItemInSlot(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public void slotClicked(EntityPlayer player, int slot, SlotClickAction action) {
		action.execute(player, getItemInSlot(slot), this, slot);
	}

}
