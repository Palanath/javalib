package pala.libs.generic.data.files;

import pala.libs.generic.data.files.PropertyObject.Property;

public class NullJSONException extends PropertyException {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	public NullJSONException(Property<?> property) {
		super(property);
	}

	public NullJSONException(String message, Property<?> property) {
		super(message, property);
	}

	public NullJSONException(String message, Throwable cause, Property<?> property) {
		super(message, cause, property);
	}

	public NullJSONException(Throwable cause, Property<?> property) {
		super(cause, property);
	}

}
