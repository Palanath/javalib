package pala.libs.generic.json;

import java.util.Collection;

import pala.libs.generic.JavaTools;

public class JSONString implements JSONValue {
	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Adds the value of each {@link JSONString} in the {@link JSONString}
	 * collection to the other collection.
	 *
	 * @param <T>
	 * @param c
	 * @param t
	 */
	public static <T extends Collection<? super String>> void addAll(final Collection<? extends JSONString> c,
			final T t) {
		for (final JSONString j : c)
			t.add(j.getValue());
	}

	private final String value;

	/**
	 * Constructs a {@link JSONString} that holds the provided {@link String}. The
	 * provided {@link String} may not be <code>null</code>.
	 *
	 * @param value The {@link String} that this {@link JSONString} should hold.
	 * @author Palanath
	 */
	public JSONString(final String value) {
		JavaTools.requireNonNull(value);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return '"' + JSONValue.escape(value) + '"';
	}

	@Override
	public String toString(final String indentation) {
		return toString();
	}
}
