package electory.client.console.commands;

import java.util.LinkedList;

import electory.client.TinyCraft;
import electory.client.console.ConsoleCommand;
import electory.client.gui.screen.GuiConsole;

public class CCHelp extends ConsoleCommand {
	private LinkedList<ConsoleCommand> commands;

	public CCHelp(TinyCraft tc, GuiConsole console, LinkedList<ConsoleCommand> commands) {
		super(tc, console, "help", "Help about the commands and about the command");
		this.commands = commands;
	}

	public void run(String[] arguments) {
		// TODO: Help about the command

		console.println("Command list:");

		for (ConsoleCommand command : commands) {
			String line = "    " + command.getName() + ": " + command.getUsage();

			console.println(line);
		}
	};
}