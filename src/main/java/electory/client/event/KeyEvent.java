package electory.client.event;

import electory.event.IEvent;
import lombok.Data;

@Data
public class KeyEvent implements IEvent {
	private final int key;
	private final boolean keyState;
}
