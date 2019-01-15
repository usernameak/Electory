package electory.client.console.commands;

import electory.client.TinyCraft;
import electory.client.console.ConsoleCommand;
import electory.client.gui.screen.GuiConsole;

public class CCExit extends ConsoleCommand {
    public CCExit(TinyCraft tc, GuiConsole console) {
        super(tc, console, "exit", "Save and exit");
	}

	public void run(String[] arguments) {
        tc.shutdown();
    };
}