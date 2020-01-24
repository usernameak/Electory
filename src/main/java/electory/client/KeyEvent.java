package electory.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class KeyEvent {
	@Getter
	private int key;
	
	@Getter
	private boolean keyState;

	@Getter
	private char keyChar;
}
