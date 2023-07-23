package pala.libs.generic.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * Represents a JSON object. This class does not support the Java
 * <code>null</code> element. Callers wishing to store the JSON null value
 * should use {@link #put(String, JSONValue)} with the {@link JSONConstant#NULL}
 * object as the value, or an appropriate put method. The put methods which take
 * reference types as the value to put will automatically insert
 * {@link JSONConstant#NULL} if the provided value is <code>null</code>.
 * </p>
 * <p>
 * This class also provides a set of <code>putNonNull</code> methods, intended
 * for inserting elements into the {@link JSONObject} only if such elements are
 * not <code>null</code>.
 * </p>
 * <p>
 * When getting a value from the {@link JSONObject}, getter methods which return
 * primitive types will throw a {@link NullPointerException} upon trying to
 * retrieve a <code>null</code> value from the specified key. Getters which
 * return a reference type will return <code>null</code> if the value being
 * obtained is <code>null</code>.
 * </p>
 * <p>
 * In all cases, getters may throw an exception if the value stored at the
 * specified key is not of the appropriate type (for example, if
 * {@link #getString(String)} is called but the value stored at the provided key
 * is a {@link JSONArray}).
 * </p>
 * 
 * @author Palanath
 *
 */
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
		if (getJConstant(key) == null)
			return null;
		else
			switch (getJConstant(key)) {
			case FALSE:
				return false;
			case TRUE:
				return true;
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

	/**
	 * <p>
	 * Gets the <code>long</code> at the provided key or throws a
	 * {@link NullPointerException} if the provided key does not exist or stores
	 * <code>null</code>.
	 * </p>
	 * <p>
	 * Note that this method may throw an exception if another data type exists at
	 * the provided key.
	 * </p>
	 * 
	 * @param key
	 * @return
	 */
	public long getLong(final String key) {
		return getJNumber(key).longValue();
	}

	public Long getLongNullable(String key) {
		JSONNumber n = getJNumber(key);
		return n == null ? null : n.longValue();
	}

	public Integer getIntNullable(String key) {
		JSONNumber n = getJNumber(key);
		return n == null ? null : n.intValue();
	}

	/**
	 * <p>
	 * Gets the {@link String} value at the provided key. If the value is
	 * <code>null</code> or the {@link JSONObject} does not contain a value at that
	 * key, <code>null</code> is returned. Otherwise, if a {@link JSONString} is
	 * stored at that key, its {@link String} value is obtained and returned.
	 * </p>
	 * 
	 * 
	 * @param key The key.
	 * @return <code>null</code> if no value exists at that key or if
	 *         <code>null</code> is contained at the key, or the {@link String}
	 *         acquired.
	 */
	public String getString(final String key) {
		JSONString s = getJString(key);
		return s == null ? null : s.getValue();
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

	/**
	 * Puts a new {@link JSONNumber} wrapping the provided {@link Integer} into the
	 * {@link JSONObject} at the specified key.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>.
	 * @return This {@link JSONObject}.
	 */
	public JSONObject put(String key, Integer value) {
		return value == null ? putNull(key) : put(key, (int) value);
	}

	/**
	 * Puts the provided {@link JSONArray} into the {@link JSONObject} at the
	 * specified key.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>.
	 */
	public JSONObject put(String key, JSONArray value) {
		put(key, value);
		return this;
	}

	/**
	 * Puts the provided {@link JSONObject} into this {@link JSONObject} at the
	 * specified key.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>.
	 */
	public JSONObject put(String key, JSONObject value) {
		put(key, value);
		return this;
	}

	public JSONObject put(final String key, final long value) {
		put(key, new JSONNumber(value));
		return this;
	}

	/**
	 * Puts the provided {@link Long} into this {@link JSONObject} at the specified
	 * key. This method wraps the provided {@link Long} in a {@link JSONNumber}
	 * unless it is <code>null</code>, in which case <code>null</code> is inserted
	 * into the {@link JSONObject} instead.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>.
	 */
	public JSONObject put(String key, Long value) {
		return value == null ? putNull(key) : put(key, (long) value);
	}

	/**
	 * Puts the provided {@link String} into this {@link JSONObject}. If the
	 * provided {@link String} is <code>null</code>, <code>null</code> is inserted
	 * into the map at the specified key. Otherwise, a new {@link JSONString} is
	 * made, wrapping the provided {@link String}, and is inserted into the
	 * {@link JSONObject} at the specified key.
	 * 
	 * @param key   The location to put the value.
	 * @param value The value, possibly <code>null</code>.
	 */
	public JSONObject put(final String key, final String value) {
		put(key, value == null ? null : new JSONString(value));
		return this;
	}

	public JSONObject putIfNonNull(final String key, final String value) {
		if (value != null)
			put(key, value);
		return this;
	}

	public JSONObject putIfNonNull(String key, Integer value) {
		if (value != null)
			put(key, value);
		return this;
	}

	public JSONObject putIfNonNull(String key, Boolean value) {
		if (value != null)
			put(key, value);
		return this;
	}

	public JSONObject putIfNonNull(String key, JSONValue value) {
		if (value != null)
			put(key, value);
		return this;
	}

	public JSONObject putIfNonNull(String key, Long value) {
		if (value != null)
			put(key, value);
		return this;
	}

	public JSONObject putNull(String key) {
		put(key, (JSONValue) null);
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
