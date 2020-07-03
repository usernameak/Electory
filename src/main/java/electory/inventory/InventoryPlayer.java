package electory.inventory;

import electory.entity.EntityPlayer;
import electory.item.ItemStack;
import electory.utils.IMetaSerializable;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

import java.io.IOException;

public class InventoryPlayer implements IInventory, IMetaSerializable {

	public InventoryPlayer(EntityPlayer player) {

	}

	private ItemStack[] stacks = new ItemStack[45];
	private int hotbarSlot = 0;

	{
		for (int i = 0; i < stacks.length; i++) {
			stacks[i] = new ItemStack();
		}
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		stacks[slot] = stack;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return stacks[slot];
	}

	public IContainerProvider createSPProvider() {
		return new ContainerProviderSP(this);
	}

	public int getSelectedSlot() {
		return hotbarSlot;
	}

	public void setSelectedSlot(int slot) {
		hotbarSlot = slot;
	}

	@Override
	public void writeToNBT(ArrayDataOutput tag) throws IOException {
		for (int i = 0; i < stacks.length; i++) {
			stacks[i].writeToNBT(tag);
		}
		tag.writeInt(hotbarSlot);
	}

	@Override
	public void readFromNBT(ArrayDataInput tag) throws IOException {
		for (int i = 0; i < stacks.length; i++) {
			stacks[i].readFromNBT(tag);
		}
		hotbarSlot = tag.readInt();
	}

	public boolean giveItem(ItemStack itemStack) {
		for (int i = 0; i < stacks.length; i++) {
			if (itemStack.transfer(stacks[i], 0)) {
				return true;
			}
		}
		return false;
	}

}
