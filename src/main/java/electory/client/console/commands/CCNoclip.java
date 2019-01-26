package electory.client.console.commands;

import electory.client.TinyCraft;
import electory.client.console.ConsoleCommand;
import electory.client.gui.screen.GuiConsole;

public class CCNoclip extends ConsoleCommand {

	public CCNoclip(TinyCraft tc, GuiConsole console) {
		super(tc, console, "noclip", "Enters the no-clipping mode");
	}

	@Override
	public void run(String[] arguments) {
		tc.player.noclip = !tc.player.noclip;
	}

}
