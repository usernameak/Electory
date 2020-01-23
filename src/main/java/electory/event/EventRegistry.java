package electory.event;

import java.util.HashMap;
import java.util.Map;

public class EventRegistry {
	private Map<String, EventType> eventTypes = new HashMap<>();
	private Map<Class<? extends IEvent>, EventType> eventTypesByClass = new HashMap<>();

	public EventType getEventByName(String name) {
		return eventTypes.get(name);
	}

	public EventType getEventByClass(Class<? extends IEvent> eventClass) {
		return eventTypesByClass.get(eventClass);
	}
	
	public void emit(IEvent event) {
		getEventByClass(event.getClass()).emit(event);
	}

	public void registerEventType(EventType type) {
		eventTypes.put(type.getName(), type);
		eventTypesByClass.put(type.getEventClass(), type);
	}
}
