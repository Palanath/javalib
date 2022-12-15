package pala.libs.generic.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

public class Listmap<K, V, L extends Collection<? super V>> extends HashMap<K, L> {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public static <K, V> Listmap<K, V, ArrayList<V>> arrayListMap() {
		return new Listmap<>(ArrayList::new);
	}

	private final Supplier<? extends L> constructor;

	public Listmap(final Supplier<? extends L> constructor) {
		this.constructor = constructor;
	}

	public boolean containsElement(final K key, final V element) {
		return containsKey(key) && get(key).contains(element);
	}

	public void putElement(final K key, final V element) {
		if (!containsKey(key))
			put(key, constructor.get());
		get(key).add(element);
	}

	public void removeElement(final K key, final V element) {
		if (containsKey(key)) {
			final L v = get(key);
			v.remove(element);
			if (v.isEmpty())
				remove(key);
		}
	}

}
