package pala.libs.generic.data.files;

import pala.libs.generic.data.files.PropertyObject.Property;
import pala.libs.generic.json.JSONValue;

/**
 * Thrown when the JSON data being loaded into a property was of the right type,
 * but could not be parsed into the value stored by the property.
 * 
 * @author Palanath
 *
 */
public class InvalidJSONException extends PropertyException {
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private final JSONValue json;

	public JSONValue getJSON() {
		return json;
	}

	public InvalidJSONException(Property<?> property, JSONValue json) {
		super(property);
		this.json = json;
	}

	public InvalidJSONException(String message, Property<?> property, JSONValue json) {
		super(message, property);
		this.json = json;
	}
}