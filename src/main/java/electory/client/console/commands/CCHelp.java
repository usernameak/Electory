package electory.client.console.commands;

import java.util.LinkedList;

import electory.client.TinyCraft;
import electory.client.console.ConsoleCommand;
import electory.client.gui.screen.GuiConsole;

public class CCHelp extends ConsoleCommand {
	private LinkedList<ConsoleCommand> commands;

	public CCHelp(TinyCraft tc, GuiConsole console, LinkedList<ConsoleCommand> commands) {
		super(tc, console, "help", "Show this menu. Use help <command> to get help about the command.");
		this.commands = commands;
		this.arguments = new String[][] { { "command", "Show help about specific command" } };
	}

	public void run(String[] arguments) {
		if (arguments.length == 0) {
			console.println("Command list:");

			for (ConsoleCommand command : commands) {
				console.println("    " + command.getName() + ": " + command.getUsage());
			}
		} else {
			for (ConsoleCommand command : commands) {
				if (command.getName().equals(arguments[0])) {
					console.println("Command \"" + command.getName() + "\":" + command.getUsage());
					console.println("Arguments:");

					for (String[] arg : command.getArguments()) {
						console.println("    " + arg[0] + ": " + arg[1]);
					}

					return;
				}
			}

			console.println("Command \"" + arguments[0] + "\" doesn't exist!");
		}
	};
}