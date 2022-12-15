package pala.libs.generic.events;

public interface EventHandler<E extends Event> {
	static <E extends Event> EventHandler<E> listener(final Runnable onFired) {
		return x -> onFired.run();
	}

	void handle(E event);

}
