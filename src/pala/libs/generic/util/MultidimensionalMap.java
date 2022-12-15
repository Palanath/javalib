package pala.libs.generic.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MultidimensionalMap<V> {
	private final int size;

	private final Map<?, ?> root = new HashMap<>();

	public MultidimensionalMap(final int size) {
		this.size = size;
	}

	public boolean contains(final Object... keys) {
		final Map<?, V> map = readMap(keys);
		return map != null && map.containsKey(keys[keys.length - 1]);
	}

	public V get(final Object... keys) {
		return read(keys);
	}

	@SuppressWarnings("unchecked")
	private Map<?, V> getMap(final Object... keys) {
		if (keys == null || keys.length > size)
			throw new IllegalArgumentException("Illegal map access.");
		Object curr = root;
		for (int i = 0; i < keys.length - 1; i++)
			if (((Map<?, V>) curr).containsKey(keys[i]))
				curr = ((Map<?, V>) curr).get(keys[i]);
			else
				((Map<Object, Object>) curr).put(keys[i], curr = new HashMap<>());
		return (Map<?, V>) curr;
	}

	@SuppressWarnings("hiding")
	public <K, V> Iterable<Entry<K, V>> iterable() {
		return MultidimensionalMap.this::iterator;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	public <K, V> Iterator<Entry<K, V>> iterator() {
		return (Iterator<Entry<K, V>>) (Iterator<?>) root.entrySet().iterator();
	}

	public V put(final V value, final Object... keys) {
		return write(value, keys);
	}

	private V read(final Object... keys) {
		final Map<?, V> map = readMap(keys);
		return map == null ? null : map.get(keys[keys.length - 1]);
	}

	/**
	 * Returns the map at the specified level, if there is one. This can be used to
	 * get a layer of this {@link MultidimensionalMap}. This method accepts any
	 * number of {@link Object} keys up to <code>{@link #size} - 1</code>.
	 *
	 * @param keys The keys for the layer.
	 * @return The layer, as a {@link Map}.
	 */
	@SuppressWarnings("unchecked")
	public Map<?, ?> readDim(final Object... keys) {
		if (keys == null || keys.length > size)
			throw new IllegalArgumentException("Illegal map access.");
		Object curr = root;
		for (final Object key : keys)
			if (((Map<?, V>) curr).containsKey(key))
				curr = ((Map<?, Map<?, ?>>) curr).get(key);
			else
				return null;
		return (Map<?, ?>) curr;
	}

	@SuppressWarnings("unchecked")
	private Map<?, V> readMap(final Object... keys) {
		if (keys == null || keys.length > size)
			throw new IllegalArgumentException("Illegal map access.");
		Object curr = root;
		for (int i = 0; i < keys.length - 1; i++)
			if (((Map<?, V>) curr).containsKey(keys[i]))
				curr = ((Map<?, V>) curr).get(keys[i]);
			else
				return null;
		return (Map<?, V>) curr;
	}

	@SuppressWarnings("unchecked")
	public V remove(final Object... keys) {
		return ((Map<Object, V>) readMap(keys)).remove(keys[keys.length - 1]);
	}

	@SuppressWarnings("unchecked")
	private V write(final V value, final Object... keys) {
		return ((Map<Object, V>) getMap(keys)).put(keys[keys.length - 1], value);
	}

}
