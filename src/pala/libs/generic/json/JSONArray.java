package pala.libs.generic.json;

import java.util.ArrayList;
import java.util.Iterator;

import pala.libs.generic.JavaTools;

public class JSONArray extends ArrayList<JSONValue> implements JSONValue {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public JSONArray(final Iterable<? extends JSONValue> values) {
		for (final JSONValue jv : values)
			add(jv);
	}

	public JSONArray(final Iterator<? extends JSONValue> values) {
		this(JavaTools.iterable(values));
	}

	public JSONArray(final JSONValue... values) {
		for (final JSONValue jv : values)
			add(jv);
	}

	@Override
	public String toString() {
		return toString("");
	}

	@Override
	public String toString(final String indentation) {
		final StringBuilder builder = new StringBuilder();
		builder.append('[');
		if (isEmpty())
			builder.append('\t');
		else {
			final Iterator<JSONValue> iterator = iterator();
			builder.append('\n').append(indentation).append('\t').append(iterator.next().toString(indentation + '\t'));
			for (; iterator.hasNext();) {
				final JSONValue v = iterator.next();
				builder.append(",\n").append(indentation).append('\t').append(v.toString(indentation + '\t'));
			}
			builder.append('\n');
		}
		builder.append(indentation).append(']');

		return builder.toString();
	}

	/**
	 * Returns an {@link Iterable} that goes over only the {@link JSONObject}s
	 * contained within this {@link JSONArray}.
	 * 
	 * @return An "object-only" {@link Iterable} of this {@link JSONArray}.
	 */
	public Iterable<JSONObject> forEachObject() {
		return JavaTools.filterToType(this, a -> !(a instanceof JSONObject));
	}

	public Iterable<JSONArray> forEachArray() {
		return JavaTools.filterToType(this, a -> !(a instanceof JSONArray));
	}

	public Iterable<JSONConstant> forEachConstant() {
		return JavaTools.filterToType(this, a -> !(a instanceof JSONConstant));
	}

	public Iterable<JSONNumber> forEachNumber() {
		return JavaTools.filterToType(this, a -> !(a instanceof JSONNumber));
	}

	public Iterable<JSONString> forEachString() {
		return JavaTools.filterToType(this, a -> !(a instanceof JSONString));
	}

}
