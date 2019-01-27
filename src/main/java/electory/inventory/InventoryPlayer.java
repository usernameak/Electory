package electory.inventory;

import electory.entity.EntityPlayer;
import electory.item.ItemStack;
import electory.nbt.CompoundTag;
import electory.nbt.ListTag;
import electory.utils.IMetaSerializable;

public class InventoryPlayer implements IInventory, IMetaSerializable {

	public InventoryPlayer(EntityPlayer player) {

	}

	private ItemStack stacks[] = new ItemStack[45];
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
	public void writeToNBT(CompoundTag tag) {
		ListTag<CompoundTag> stacksList = new ListTag<>(CompoundTag.class);
		for (int i = 0; i < stacks.length; i++) {
			CompoundTag stackTag = new CompoundTag();
			stacks[i].writeToNBT(stackTag);
			stacksList.add(stackTag);
		}
		tag.put("stacks", stacksList);
		tag.putInt("hotbarSlot", hotbarSlot);
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		@SuppressWarnings("unchecked")
		ListTag<CompoundTag> stacksList = (ListTag<CompoundTag>) tag.getListTag("stacks");

		try {
			for (int i = 0; i < stacks.length; i++) {
				CompoundTag stackTag = stacksList.get(i);
				stacks[i].readFromNBT(stackTag);
			}
		} catch (IndexOutOfBoundsException e) {
			// nothing
		}
		hotbarSlot = tag.getInt("hotbarSlot");
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
