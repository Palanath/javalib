package pala.libs.generic.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventManager<E extends Event> {

	private class Registration<T extends E> {
		private final EventType<T> type;
		private final EventHandler<? super T> handler;
		private final boolean remove;

		public Registration(final EventType<T> type, final EventHandler<? super T> handler) {
			this.type = type;
			this.handler = handler;
			remove = false;
		}

		public Registration(final EventType<T> type, final EventHandler<? super T> handler, final boolean remove) {
			this.type = type;
			this.handler = handler;
			this.remove = remove;
		}

	}

	private final Map<EventType<? extends E>, Collection<EventHandler<?>>> handlerMap = new HashMap<>();
	private final List<Registration<?>> modq = new ArrayList<>();

	private volatile boolean iterating;

	private <T extends E> void add(final EventType<T> type, final EventHandler<? super T> handler,
			final Map<EventType<? extends E>, Collection<EventHandler<?>>> map) {
		if (map.containsKey(type))
			map.get(type).add(handler);
		else {
			final ArrayList<EventHandler<?>> handlers = new ArrayList<>();
			map.put(type, handlers);
			handlers.add(handler);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends E> void fire(final EventType<T> type, final T event) {
		EventType<E> currType = (EventType<E>) type;
		while (currType != null) {
			if (handlerMap.containsKey(currType)) {
				iterating = true;
				for (final EventHandler<?> eh : handlerMap.get(currType))
					try {
						((EventHandler<E>) eh).handle(event);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				iterating = false;
				for (final Registration<?> reg : modq)
					if (reg.remove)
						remove(reg.type, ((Registration<E>) reg).handler, handlerMap);
					else
						add(reg.type, ((Registration<E>) reg).handler, handlerMap);
				modq.clear();
			}
			currType = event.isConsumed() ? null : (EventType<E>) currType.getParent();
		}
	}

	public <T extends E> void register(final EventType<T> type, final EventHandler<? super T> handler) {
		if (iterating)
			modq.add(new Registration<>(type, handler));
		else
			add(type, handler, handlerMap);
	}

	private <T extends E> boolean remove(final EventType<T> type, final EventHandler<? super T> handler,
			final Map<EventType<? extends E>, Collection<EventHandler<?>>> map) {
		if (map.containsKey(type)) {
			final Collection<EventHandler<?>> handlers = map.get(type);
			final boolean rem = handlers.remove(handler);
			if (handlers.isEmpty())
				map.remove(type);
			return rem;
		}
		return false;
	}

	public <T extends E> void unregister(final EventType<T> type, final EventHandler<? super T> handler) {
		if (iterating)
			modq.add(new Registration<>(type, handler, true));
		else
			remove(type, handler, handlerMap);
	}

}
