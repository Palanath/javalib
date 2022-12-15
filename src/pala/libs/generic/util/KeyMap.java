package pala.libs.generic.util;

import java.io.Serializable;
import java.util.Map;
import java.util.WeakHashMap;

public class KeyMap<V, M extends Map<KeyMap.Key<? extends V>, V>> implements Serializable {

	public static class Key<KV> {
		private Key() {
		}

		public KV get(final KeyMap<? super KV, ?> map) {
			return map.get(this);
		}

		public KV put(final KeyMap<? super KV, ?> map, final KV data) {
			return map.put(this, data);
		}
	}

	public class LocalKey<KV extends V> {
		public final Key<KV> key;

		public LocalKey(final Key<KV> key) {
			this.key = key;
		}

		public KV get() {
			return key.get(KeyMap.this);
		}

		@SuppressWarnings("unchecked")
		public KV put(final KV data) {
			return (KV) KeyMap.this.data.put(key, data);
		}
	}

	public static class OptionalKey<KV> extends Key<KV> {
		public boolean exists(final KeyMap<?, ?> map) {
			return map.containsKey(this);
		}
	}

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	protected static <V> Key<V> key() {
		return new Key<>();
	}

	public static <V> KeyMap<V, WeakHashMap<KeyMap.Key<? extends V>, V>> keyMap() {
		return new KeyMap<>(new WeakHashMap<>());
	}

	public static <V, M extends Map<Key<? extends V>, V>> KeyMap<V, M> keyMap(final M map) {
		return new KeyMap<>(map);
	}

	private final M data;

	protected KeyMap(final M map) {
		this.data = map;
	}

	public boolean containsKey(final Key<?> key) {
		return data.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	public <T extends V> T get(final Key<T> key) {
		return (T) data.get(key);
	}

	/**
	 * Returns the {@link WeakHashMap} that backs this {@link KeyMap}. Care should
	 * be taken when adding data to the map via the raw {@link WeakHashMap} returned
	 * by this method, as not honoring the type parameters laced with variables
	 * holding {@link Key} objects may cause problems when retrieving values using
	 * those {@link Key} objects.
	 *
	 * @return The {@link WeakHashMap} that backs this {@link KeyMap}.
	 */
	public M getData() {
		return data;
	}

	/**
	 * Checks if this {@link KeyMap} can be {@link Serializable serialized} by
	 * checking if all values are {@link Serializable}.
	 *
	 * @return <code>true</code> if every value in {@link #data} is an
	 *         {@code instanceof} {@link Serializable}.
	 */
	public boolean isSerializable() {
		for (final Object o : data.values())
			if (!(o instanceof Serializable))
				return false;
		return true;
	}

	public <KV extends V> LocalKey<KV> lk() {
		return lk(new Key<>());
	}

	public <KV extends V> LocalKey<KV> lk(final Key<KV> key) {
		return new LocalKey<>(key);
	}

	/**
	 * A convenience method to create a {@link LocalKey} and assign a value to it.
	 *
	 * @param <KV>  The type of the value of the key.
	 * @param key   The {@link Key} object to back this {@link LocalKey}.
	 * @param value The value to assign to this map with the specified key.
	 * @return The new {@link LocalKey}.
	 */
	public <KV extends V> LocalKey<KV> lk(final Key<KV> key, final KV value) {
		final LocalKey<KV> lk = lk(key);
		key.put(this, value);
		return lk;
	}

	@SuppressWarnings("unchecked")
	public <T extends V> T put(final Key<T> key, final T data) {
		return (T) this.data.put(key, data);
	}

	public <T extends V> Key<T> put(final T data) {
		final Key<T> key = key();
		put(key, data);
		return key;
	}

}
