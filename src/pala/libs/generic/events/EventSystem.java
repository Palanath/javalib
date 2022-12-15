package pala.libs.generic.events;

public interface EventSystem<E extends Event> {
	EventManager<E> getEventManager();
}
