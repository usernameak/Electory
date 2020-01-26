package electory.event;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class EventType {
	@Getter
	private String name;
	
	private List<IEventHandler> handlers = new ArrayList<>();
	
	@Getter
	private Class<? extends IEvent> eventClass;
	
	public EventType(String name, Class<? extends IEvent> eventClass) {
		this.name = name;
		this.eventClass = eventClass;
	}
	
	public void registerHandler(IEventHandler handler) {
		handlers.add(handler);
	}

	public void emit(IEvent event) {
		handlers.forEach(h -> h.invoke(event));
	}

	public void unregisterHandler(IEventHandler handler) {
		handlers.remove(handler);
	}
}
