package pala.libs.generic.json;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

public interface JSONValue extends Serializable {
	/**
	 * Adds the JSON {@link String} form of the objects in the given
	 * {@link JSONValue} {@link Collection} to the other collection.
	 *
	 * @param <T>
	 * @param c
	 * @param t
	 */
	static <T extends Collection<? super String>> void addAll(final Collection<? extends JSONValue> c, final T t) {
		for (final JSONValue j : c)
			t.add(j.toString());
	}

	static String escape(final String input) {
		return input.replace("\\", "\\\\").replace("\"", "\\\"")
				// .replace("/", "\\/")
				.replace("\b", "\\b").replace("\f", "\\f").replace("\r", "\\r").replace("\t", "\\t")
				.replace("\n", "\\n");
	}

	static String toStringShort(final JSONValue v) {
		if (v instanceof JSONArray) {
			final StringBuilder sb = new StringBuilder("[");
			final Iterator<JSONValue> iterator = ((JSONArray) v).iterator();
			if (iterator.hasNext()) {
				sb.append(toStringShort(iterator.next()));
				for (; iterator.hasNext();)
					sb.append(',').append(toStringShort(iterator.next()));
			}
			sb.append(']');
			return sb.toString();
		}
		if (v instanceof JSONObject) {
			final StringBuilder sb = new StringBuilder("{");
			final Iterator<Entry<String, JSONValue>> iterator = ((JSONObject) v).entrySet().iterator();
			if (iterator.hasNext()) {
				Entry<String, JSONValue> item = iterator.next();
				sb.append('"').append(escape(item.getKey())).append("\":").append(toStringShort(item.getValue()));
				for (; iterator.hasNext();)
					sb.append(',').append('"').append(escape((item = iterator.next()).getKey())).append("\":")
							.append(toStringShort(item.getValue()));
			}
			sb.append('}');
			return sb.toString();
		} else
			return v.toString();
	}

	String toString(String indentation);
}
