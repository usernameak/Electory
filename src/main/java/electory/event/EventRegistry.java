package electory.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;

public class EventRegistry {
	private Map<String, EventType> eventTypes = new HashMap<>();
	private Map<Class<? extends IEvent>, EventType> eventTypesByClass = new HashMap<>();
	private Map<Object, MultiValuedMap<EventType, IEventHandler>> cEventHandlers = new HashMap<>();

	public EventType getEventByName(String name) {
		return eventTypes.get(name);
	}

	public EventType getEventByClass(Class<? extends IEvent> eventClass) {
		return eventTypesByClass.get(eventClass);
	}

	public void emit(IEvent event) {
		getEventByClass(event.getClass()).emit(event);
	}

	@SuppressWarnings("unchecked")
	public void registerHandler(Object handler) {
		synchronized (cEventHandlers) {
			MultiValuedMap<EventType, IEventHandler> hmap = MultiMapUtils.newSetValuedHashMap();
			for (Method method : handler.getClass().getMethods()) {
				HandleEvent annotation = method.getAnnotation(HandleEvent.class);
				if (annotation == null)
					continue;
				if (method.getParameterCount() != 1) {
					throw new IllegalArgumentException("@HandleEvent methods should have exactly one argument");
				}
				Class<?> eventTypeCls = method.getParameterTypes()[0];
				if (!eventTypesByClass.containsKey(eventTypeCls)) {
					throw new IllegalArgumentException("event type " + eventTypeCls.getName() + " is not registered");
				}
				EventType eventType = eventTypesByClass.get((Class<? extends IEvent>) eventTypeCls);
				IEventHandler chandler = new IEventHandler() {
					@Override
					public void invoke(IEvent event) {
						try {
							method.invoke(handler, event);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}
				};
				eventType.registerHandler(chandler);
				hmap.put(eventType, chandler);
			}
			cEventHandlers.put(handler, hmap);
		}
	}

	public void unregisterHandler(Object handler) {
		synchronized (cEventHandlers) {
			MultiValuedMap<EventType, IEventHandler> hmap = cEventHandlers.remove(handler);
			for(Map.Entry<EventType, IEventHandler> entry : hmap.entries()) {
				entry.getKey().unregisterHandler(entry.getValue());
			}
		}
	}

	public void registerEventType(EventType type) {
		eventTypes.put(type.getName(), type);
		eventTypesByClass.put(type.getEventClass(), type);
	}
}
