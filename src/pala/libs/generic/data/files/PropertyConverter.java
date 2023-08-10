package pala.libs.generic.data.files;

import pala.libs.generic.json.JSONString;
import pala.libs.generic.json.JSONValue;

public interface PropertyConverter<V> {
	/**
	 * <p>
	 * Converts from JSON to a value indicated by the type of this
	 * {@link PropertyConverter}.
	 * </p>
	 * <p>
	 * This method may be called with <code>null</code> or with
	 * {@link PropertyObject#NOT_WRITTEN}.
	 * </p>
	 *
	 * @param json The JSON data to convert.
	 * @return The converted value.
	 * @throws PropertyException An exception if the JSON cannot be converted.
	 */
	V fromJSON(JSONValue json) throws PropertyException;

	/**
	 * Converts the provided property value to JSON data that can be saved. This
	 * method is free to return <code>null</code> or
	 * {@link PropertyObject#NOT_WRITTEN}.
	 *
	 * @param value The value to convert to JSON.
	 * @return The JSON data, possibly <code>null</code> or
	 *         {@link PropertyObject#NOT_WRITTEN}.
	 */
	JSONValue toJSON(V value);

	/**
	 * <p>
	 * Casts the provided {@link JSONValue} to a {@link JSONString}, calls
	 * {@link JSONString#getValue()}, then returns the result. This method will
	 * throw exceptions if the {@link String} value cannot be obtained.
	 * </p>
	 * <p>
	 * This method is primarily used for {@link PropertyConverter} implementations
	 * to conveniently obtain a {@link String} or throw an appropriate exception if
	 * they cannot.
	 * </p>
	 * 
	 * @param value The {@link JSONValue}.
	 * @return The {@link String} value in the {@link JSONValue}.
	 * @throws PropertyRequiredException If the provided {@link JSONValue} is
	 *                                   {@link PropertyObject#NOT_WRITTEN}.
	 * @throws JSONCastException         If the provided {@link JSONValue} is not
	 *                                   assignable to {@link JSONString}.
	 * @throws NullJSONException         If the provided {@link JSONValue} is
	 *                                   <code>null</code>.
	 */
	static String convertToString(JSONValue value)
			throws JSONCastException, NullJSONException, PropertyRequiredException {
		if (value == PropertyObject.NOT_WRITTEN)
			throw new PropertyRequiredException(null);
		try {
			return ((JSONString) value).getValue();
		} catch (ClassCastException e) {
			throw new JSONCastException(e);
		} catch (NullPointerException e) {
			throw new NullJSONException(e);
		}
	}
}