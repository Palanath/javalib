package pala.libs.generic.guis;

import java.util.WeakHashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import pala.libs.generic.util.KeyMap;
import pala.libs.generic.util.ObservableKeyMap;

public class ApplicationProperties extends ObservableKeyMap<Object, ObservableMap<KeyMap.Key<?>, Object>> {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public final static Key<String> THEME_STYLESHEET = key();

	public final LocalKey<String> themeStylesheet = lk(THEME_STYLESHEET);

	public ApplicationProperties() {
		super(FXCollections.observableMap(new WeakHashMap<>()));
	}

}
