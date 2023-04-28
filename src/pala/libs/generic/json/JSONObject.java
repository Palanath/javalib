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

	public Boolean getBooleanNullable(String key) {
		switch (getJConstant(key)) {
		case FALSE:
			return false;
		case TRUE:
			return true;
		case NULL:
			return null;
		default:
			throw new ClassCastException("The JSON Constant stored for the key, " + key
					+ ", is not a boolean nor null, but an attempt was made to access it as such.");
		}
	}

	public int getInt(final String key) {
		return getJNumber(key).intValue();
	}

	public JSONArray getJArray(String key) {
		return (JSONArray) get(key);
	}

	public JSONConstant getJConstant(final String key) {
		return (JSONConstant) get(key);
	}

	public JSONNumber getJNumber(final String key) {
		return (JSONNumber) get(key);
	}

	public JSONObject getJObject(String key) {
		return (JSONObject) get(key);
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

	public JSONObject put(final String key, final boolean value) {
		put(key, value ? JSONConstant.TRUE : JSONConstant.FALSE);
		return this;
	}

	public JSONObject put(String key, Boolean value) {
		put(key, value == null ? null : value ? JSONConstant.TRUE : JSONConstant.FALSE);
		return this;
	}

	public JSONObject put(final String key, final int value) {
		put(key, new JSONNumber(value));
		return this;
	}

	public JSONObject putNull(String key) {
		put(key, JSONConstant.NULL);
		return this;
	}

	/**
	 * If the provided {@link String} value is <code>null</code>,
	 * {@link #put(String, JSONValue)}s {@link JSONConstant#NULL} into the map,
	 * otherwise, puts the a new {@link JSONNumber} wrapping the provided
	 * {@link Integer}.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>, in which case
	 *              {@link JSONConstant#NULL} is placed instead of a new
	 *              {@link JSONNumber}.
	 * @return This {@link JSONObject}.
	 */
	public JSONObject put(String key, Integer value) {
		return value == null ? putNull(key) : put(key, (int) value);
	}

	public JSONObject put(final String key, final long value) {
		put(key, new JSONNumber(value));
		return this;
	}

	/**
	 * If the provided {@link String} value is <code>null</code>,
	 * {@link #put(String, JSONValue)}s {@link JSONConstant#NULL} into the map,
	 * otherwise, puts the a new {@link JSONNumber} wrapping the provided
	 * {@link Long}.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>, in which case
	 *              {@link JSONConstant#NULL} is placed instead of a new
	 *              {@link JSONNumber}.
	 * @return This {@link JSONObject}.
	 */
	public JSONObject put(String key, Long value) {
		return value == null ? putNull(key) : put(key, (long) value);
	}

	/**
	 * If the provided {@link String} value is <code>null</code>,
	 * {@link #put(String, JSONValue)}s {@link JSONConstant#NULL} into the map,
	 * otherwise, puts the provided {@link JSONObject}.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>, in which case
	 *              {@link JSONConstant#NULL} is placed instead of the
	 *              {@link JSONObject}.
	 * @return This {@link JSONObject}.
	 */
	public JSONObject put(String key, JSONObject value) {
		put(key, value == null ? JSONConstant.NULL : value);
		return this;
	}

	/**
	 * If the provided {@link String} value is <code>null</code>,
	 * {@link #put(String, JSONValue)}s {@link JSONConstant#NULL} into the map,
	 * otherwise, puts the provided {@link JSONArray}.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>, in which case
	 *              {@link JSONConstant#NULL} is placed instead of the
	 *              {@link JSONArray}.
	 * @return This {@link JSONObject}.
	 */
	public JSONObject put(String key, JSONArray value) {
		put(key, value == null ? JSONConstant.NULL : value);
		return this;
	}

	/**
	 * If the provided {@link String} value is <code>null</code>,
	 * {@link #put(String, JSONValue)}s {@link JSONConstant#NULL} into the map,
	 * otherwise, puts the a new {@link JSONString} wrapping the provided
	 * {@link String}.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>, in which case
	 *              {@link JSONConstant#NULL} is placed instead of a new
	 *              {@link JSONString}.
	 * @return This {@link JSONObject}.
	 */
	public JSONObject put(final String key, final String value) {
		put(key, value == null ? JSONConstant.NULL : new JSONString(value));
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
