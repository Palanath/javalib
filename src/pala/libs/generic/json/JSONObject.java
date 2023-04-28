package pala.libs.generic.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONObject extends HashMap<String, JSONValue> implements JSONValue {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public JSONObject() {
	}

	public JSONObject(final Map<? extends String, ? extends JSONValue> map) {
		super(map);
	}

	/**
	 * Returns a shallow copy of this {@link JSONObject}.
	 */
	@Override
	public JSONObject clone() {
		return new JSONObject(this);
	}

	public boolean getBoolean(final String key) {
		switch (getJConstant(key)) {
		case FALSE:
			return false;
		case TRUE:
			return true;
		default:
			throw new ClassCastException("The JSON Constant stored for the key, " + key
					+ ", is not a boolean, but an attempt was made to access it as one.");
		}
	}

	public int getInt(final String key) {
		return getJNumber(key).intValue();
	}

	public JSONConstant getJConstant(final String key) {
		return (JSONConstant) get(key);
	}

	public JSONNumber getJNumber(final String key) {
		return (JSONNumber) get(key);
	}

	public JSONString getJString(final String key) {
		return (JSONString) get(key);
	}

	public long getLong(final String key) {
		return getJNumber(key).longValue();
	}

	public String getString(final String key) {
		return getJString(key).getValue();
	}

	public JSONArray getJArray(String key) {
		return (JSONArray) get(key);
	}

	public JSONObject put(final String key, final boolean value) {
		put(key, value ? JSONConstant.TRUE : JSONConstant.FALSE);
		return this;
	}

	public JSONObject put(final String key, final int value) {
		put(key, new JSONNumber(value));
		return this;
	}

	public JSONObject put(final String key, final long value) {
		put(key, new JSONNumber(value));
		return this;
	}

	public JSONObject put(final String key, final String value) {
		put(key, new JSONString(value));
		return this;
	}

	public JSONObject putIfNonNull(final String key, final String value) {
		if (value != null)
			put(key, value);
		return this;
	}

	@Override
	public String toString() {
		return toString("");
	}

	@Override
	public String toString(final String indentation) {
		final StringBuilder builder = new StringBuilder();
		builder.append('{');
		if (isEmpty())
			builder.append('\t');
		else {
			final Iterator<Entry<String, JSONValue>> iterator = entrySet().iterator();
			Entry<String, JSONValue> e = iterator.next();
			builder.append('\n').append(indentation).append('\t').append('"').append(JSONValue.escape(e.getKey()))
					.append('"').append(':').append(e.getValue().toString(indentation + '\t'));
			for (; iterator.hasNext();) {
				e = iterator.next();
				builder.append(",\n").append(indentation).append('\t').append('"').append(e.getKey()).append('"')
						.append(':').append(e.getValue().toString(indentation + '\t'));
			}
			builder.append('\n');
		}
		builder.append(indentation).append('}');

		return builder.toString();
	}

}
