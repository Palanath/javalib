package pala.libs.generic.data.files;

import pala.libs.generic.data.files.PropertyObject.Property;

public class PropertyRequiredException extends PropertyException {
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	public PropertyRequiredException(Property<?> property) {
		super(property);
	}

	public PropertyRequiredException(String message, Property<?> property) {
		super(message, property);
	}
}