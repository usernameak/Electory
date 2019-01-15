package electory.client.console.commands;

import electory.client.TinyCraft;
import electory.client.console.ConsoleCommand;
import electory.client.gui.screen.GuiConsole;

public class CCTp extends ConsoleCommand {

	public CCTp(TinyCraft tc, GuiConsole console) {
		super(tc, console, "tp", "Teleport player. tp <x> <y> <z>");
	}

	@Override
	public void run(String[] arguments) {
		if (arguments.length != 3) {
			this.console.println("Usage: tp <x> <y> <z>");
		} else {
			try {
				tc.player.setPosition(	Double.parseDouble(arguments[0]),
										Double.parseDouble(arguments[1]),
										Double.parseDouble(arguments[2]),
										false);
			} catch (NumberFormatException e) {
				this.console.println("Usage: tp <x> <y> <z>");
			}
		}
	}

}
