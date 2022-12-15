package pala.libs.generic.util;

import java.util.Map;

public final class MapProperty<K, V> {
	private final Map<K, V> map;
	private final K key;

	public MapProperty(final Map<K, V> map, final K key) {
		this.map = map;
		this.key = key;
	}

	public V get() {
		return map.get(key);
	}

	public V put(final V value) {
		return map.put(key, value);
	}
}
