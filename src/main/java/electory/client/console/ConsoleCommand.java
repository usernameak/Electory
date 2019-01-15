package electory.client.console;

import electory.client.TinyCraft;
import electory.client.gui.screen.GuiConsole;

public abstract class ConsoleCommand {
	protected final TinyCraft tc;
	protected final GuiConsole console;
	
	protected String name; // Trigger name
	protected String usage; // Usage or description
	protected String[][] arguments = new String[0][2]; // [ [String name, String description] ]

	public ConsoleCommand(TinyCraft tc, GuiConsole console, String name, String usage) {
		this.tc = tc;
		this.console = console;
		this.name = name;
		this.usage = usage;
	}

	public String getName() {
		return name;
	}

	public String getUsage() {
		return usage;
	}

	public String[] getArgument(int number) {
		return arguments[number];
	}

	public String[][] getArguments() {
		return arguments;
	}

	public abstract void run(String[] arguments);
}