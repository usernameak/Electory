package electory.client.console.commands;

import electory.client.TinyCraft;
import electory.client.console.ConsoleCommand;
import electory.client.gui.screen.GuiConsole;
import electory.item.Item;
import electory.item.ItemStack;

public class CCGive extends ConsoleCommand {

    public CCGive(TinyCraft tc, GuiConsole console) {
        super(tc, console, "give", "give <itemname>");
    }

    @Override
    public void run(String[] arguments) {
        tc.player.inventory.giveItem(new ItemStack(Item.REGISTRY.get(arguments[0]), 32));
    }

}
