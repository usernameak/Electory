package electory.client.console;

import java.util.Arrays;
import java.util.LinkedList;

import electory.client.TinyCraft;
import electory.client.console.commands.CCClear;
import electory.client.console.commands.CCExit;
import electory.client.console.commands.CCFly;
import electory.client.console.commands.CCHelp;
import electory.client.console.commands.CCNoclip;
import electory.client.console.commands.CCTp;
import electory.client.gui.screen.GuiConsole;

public class Console {
	public final GuiConsole gui;
	private final TinyCraft tc;
	private final LinkedList<ConsoleCommand> consoleCommands = new LinkedList<>();

	public Console(TinyCraft tc, GuiConsole console) {
		super();
		this.tc = tc;
		this.gui = console;
	}

	public void init() { // FIXME: Bad practics
		addCommand(new CCClear(tc, gui));
		addCommand(new CCExit(tc, gui));
		addCommand(new CCHelp(tc, gui, consoleCommands));
		addCommand(new CCTp(tc, gui));
		addCommand(new CCNoclip(tc, gui));
		addCommand(new CCFly(tc, gui));
	}

	public void addCommand(ConsoleCommand command) {
		consoleCommands.push(command);
	}

	public void execCommand(String commandline) {
		String[] tokens = commandline.split("\\s+");
		
		if(tokens.length == 0) return;

		String cmd = tokens[0];
		String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
		
		tc.logger.info("> " + commandline);

		for (ConsoleCommand command : consoleCommands) {
			if(command.getName().equals(cmd)) {
				command.run(args);

				return;
			}
		}

		gui.println("Command \"" + cmd + "\" doesn't exist!");
	}
}
