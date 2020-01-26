package electory.inventory;

import electory.entity.EntityPlayer;
import electory.item.ItemStack;

public enum SlotClickAction {
	CLICK_ALL {
		@Override
		public void execute(EntityPlayer player, ItemStack stack, IContainerProvider container, int slot) {
			if (player.stackOnCursor.isEmpty()) {
				stack.transfer(player.stackOnCursor, 0);
			} else {
				player.stackOnCursor.transfer(stack, 0);
			}
		}
	},
	CLICK_PARTIAL {
		@Override
		public void execute(EntityPlayer player, ItemStack stack, IContainerProvider container, int slot) {
			if (player.stackOnCursor.isEmpty()) {
				stack.transfer(player.stackOnCursor, (stack.count + 1) / 2);
			} else {
				player.stackOnCursor.transfer(stack, 1);
			}
		}
	};

	public abstract void execute(EntityPlayer player, ItemStack stack, IContainerProvider container, int slot);
}
