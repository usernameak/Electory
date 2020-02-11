package electory.client.console.commands;

import electory.client.TinyCraft;
import electory.client.console.ConsoleCommand;
import electory.client.gui.screen.GuiConsole;

public class CCFly extends ConsoleCommand {

	public CCFly(TinyCraft tc, GuiConsole console) {
		super(tc, console, "fly", "Enters the fly mode");
	}

	@Override
	public void run(String[] arguments) {
		tc.player.fly = !tc.player.fly;
	}

}
