package electory.client.console;

import java.util.Arrays;
import java.util.LinkedList;

import electory.client.TinyCraft;
import electory.client.console.commands.CCClear;
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
	}

	public void addCommand(ConsoleCommand command) {
		consoleCommands.push(command);
	}

	public void execCommand(String commandline) {
		String[] tokens = commandline.split("\\s+");
		
		if(tokens.length == 0) return;

		String cmd = tokens[0];
		String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
		
		System.out.println("> " + commandline);

		for (ConsoleCommand command : consoleCommands) {
			if(command.getName().equals(cmd)) {
				command.run(args);
			}
		}
	}
}
