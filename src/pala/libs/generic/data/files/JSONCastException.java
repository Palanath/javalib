package pala.libs.generic.data.files;

import pala.libs.generic.data.files.PropertyObject.Property;
import pala.libs.generic.data.files.PropertyObject.PropertyConverter;
import pala.libs.generic.json.JSONValue;

/**
 * Represents a {@link ClassCastException} that was raised while trying to cast
 * a {@link JSONValue} to the expected sub-type. This is raised primarily by
 * {@link PropertyConverter}s or by a {@link Property} itself when loading from
 * JSON data.
 * 
 * @author Palanath
 *
 */
public class JSONCastException extends PropertyException {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	public JSONCastException(Property<?> property) {
		super(property);
	}

	public JSONCastException(String message, ClassCastException cause, Property<?> property) {
		super(message, cause, property);
	}

	public JSONCastException(ClassCastException cause, Property<?> property) {
		super(cause, property);
	}

}
