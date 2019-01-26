package electory.inventory;

import electory.entity.EntityPlayer;
import electory.item.ItemStack;

public class ContainerProviderSP implements IContainerProvider {

	private IInventory inv;

	public ContainerProviderSP(IInventory inv) {
		this.inv = inv;
	}

	@Override
	public void pickupItemFromSlot(EntityPlayer player, int slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putItemToSlot(EntityPlayer player, int slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemStack getItemInSlot(int slot) {
		return inv.getStackInSlot(slot);
	}

}
