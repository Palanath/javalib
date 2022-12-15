package pala.libs.generic.util;

import java.util.Map;
import java.util.WeakHashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import pala.libs.generic.util.KeyMap.Key;

public class ObservableKeyMap<V, M extends ObservableMap<Key<? extends V>, V>> extends KeyMap<V, M> {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public static <V> ObservableKeyMap<V, ObservableMap<KeyMap.Key<? extends V>, V>> observableKeyMap() {
		return observableKeyMap(new WeakHashMap<>());
	}

	public static <V, M extends ObservableMap<Key<? extends V>, V>> ObservableKeyMap<V, M> observableKeyMap(
			final M map) {
		return new ObservableKeyMap<>(map);
	}

	public static <V> ObservableKeyMap<V, ObservableMap<KeyMap.Key<? extends V>, V>> observableKeyMap(
			final Map<Key<? extends V>, V> map) {
		return observableKeyMap(FXCollections.observableMap(map));
	}

	// TODO

	public ObservableKeyMap(final M map) {
		super(map);
	}

}
