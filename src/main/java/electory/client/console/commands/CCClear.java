package electory.client.console.commands;

import electory.client.TinyCraft;
import electory.client.console.ConsoleCommand;
import electory.client.gui.screen.GuiConsole;

public class CCClear extends ConsoleCommand {
    public CCClear(TinyCraft tc, GuiConsole console) {
        super(tc, console, "clear", "Clear the console");
	}

	public void run(String[] arguments) {
        System.out.println("Clear the console");
        console.clear();
    };
}