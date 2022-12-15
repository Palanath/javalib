package pala.libs.generic.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Cache<K, V> {

	private final Timer timer;
	private final Map<K, V> map;
	private TimerTask task = genTask();
	private final long millis = 300000;

	public Cache() {
		this(new Timer());
	}

	public Cache(final int initialCapacity) {
		this(initialCapacity, new Timer());
	}

	public Cache(final int initialCapacity, final float loadFactor) {
		this(initialCapacity, loadFactor, new Timer());
	}

	public Cache(final int initialCapacity, final float loadFactor, final Timer timer) {
		map = new HashMap<>(initialCapacity, loadFactor);
		this.timer = timer;
	}

	public Cache(final int initialCapacity, final Timer timer) {
		map = new HashMap<>(initialCapacity);
		this.timer = timer;
	}

	public Cache(final Map<? extends K, ? extends V> m) {
		this(m, new Timer());
	}

	public Cache(final Map<? extends K, ? extends V> m, final Timer timer) {
		map = new HashMap<>(m);
		this.timer = timer;
	}

	public Cache(final Timer timer) {
		this.timer = timer;
		map = new HashMap<>();
	}

	public Cache(final Timer timer, final Map<K, V> map) {
		this.timer = timer;
		this.map = map;
	}

	public void clear() {
		map.clear();
		disableTimer();
	}

	public boolean containsKey(final Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(final Object value) {
		return map.containsValue(value);
	}

	protected abstract Collection<? extends K> deadEntries();

	private void disableTimer() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	private void enableTimer() {
		if (task == null) {
			task = genTask();
			timer.schedule(task, millis, millis);
		}
	}

	public Set<Entry<K, V>> entries() {
		return Collections.unmodifiableSet(map.entrySet());
	}

	private TimerTask genTask() {
		return new TimerTask() {

			@Override
			public void run() {
				map.keySet().removeAll(deadEntries());
			}
		};
	}

	public V get(final Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keys() {
		return Collections.unmodifiableSet(map.keySet());
	}

	public V put(final K key, final V value) {
		final V v = map.put(key, value);
		enableTimer();
		return v;
	}

	public void putAll(final Map<? extends K, ? extends V> m) {
		map.putAll(m);
		if (!isEmpty())
			enableTimer();
	}

	public V remove(final Object key) {
		final V v = map.remove(key);
		if (isEmpty())
			disableTimer();
		return v;
	}

	public boolean remove(final Object key, final Object value) {
		final boolean res = map.remove(key, value);
		if (isEmpty())
			disableTimer();
		return res;
	}

	public int size() {
		return map.size();
	}

	public Collection<V> values() {
		return Collections.unmodifiableCollection(map.values());
	}

}
