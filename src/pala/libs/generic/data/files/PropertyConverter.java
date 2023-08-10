package pala.libs.generic.data.files;

import java.time.Instant;

import pala.libs.generic.data.files.PropertyObject.SimpleProperty;
import pala.libs.generic.json.JSONConstant;
import pala.libs.generic.json.JSONString;
import pala.libs.generic.json.JSONValue;

public interface PropertyConverter<V> {
	/**
	 * <p>
	 * A non-<code>null</code>able, required, {@link Instant} property converter.
	 * This converter can be safely used with {@link SimpleProperty
	 * SimpleProperties} that have a default value.
	 * </p>
	 */
	PropertyConverter<Instant> INSTANT_PROPERTY_CONVERTER = new PropertyConverter<>() {
	
		@Override
		public Instant fromJSON(final JSONValue json) throws PropertyException {
			return Instant.parse(PropertyConverter.convertToString(json));
		}
	
		@Override
		public JSONValue toJSON(final Instant value) {
			return new JSONString(value.toString());
		}
	};
	/**
	 * <p>
	 * A non-<code>null</code>able, reqiured, <code>boolean</code> property
	 * converter. This is the property converter for the official primitive boolean
	 * type.
	 * </p>
	 * <p>
	 * This converter throws a {@link PropertyRequiredException} if an attempt is
	 * made to convert {@link PropertyObject#NOT_WRITTEN} to a {@link Boolean} using
	 * it.
	 * </p>
	 */
	PropertyConverter<Boolean> BOOLEAN_PROPERTY_CONVERTER = new PropertyConverter<>() {
		@Override
		public Boolean fromJSON(final JSONValue json) throws PropertyException {
			if (json == PropertyObject.NOT_WRITTEN)
				throw new PropertyRequiredException(null);
			try {
				return (JSONConstant) json == JSONConstant.TRUE;
			} catch (final ClassCastException e) {
				throw new JSONCastException(e);
			}
		}
	
		@Override
		public JSONValue toJSON(final Boolean value) {
			return value ? JSONConstant.TRUE : JSONConstant.FALSE;
		}
	};
	/**
	 * <p>
	 * A non-<code>null</code>able, required, {@link String} property converter.
	 * </p>
	 * <p>
	 * This class converts between {@link String}s and {@link JSONString}s.
	 * </p>
	 * <ul>
	 * <li>It throws a {@link PropertyRequiredException} if the provided
	 * {@link JSONValue} is {@link PropertyObject#NOT_WRITTEN} and</li>
	 * <li>a {@link JSONCastException} if the provided {@link JSONValue} is not a
	 * {@link JSONString}.</li>
	 * </ul>
	 */
	PropertyConverter<String> STRING_PROPERTY_CONVERTER = new PropertyConverter<>() {
		@Override
		public String fromJSON(final JSONValue json) throws PropertyException {
			return PropertyConverter.convertToString(json);
		}
	
		@Override
		public JSONValue toJSON(final String value) {
			return new JSONString(value);
		}
	};

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