package pala.libs.generic.data.files;

import pala.libs.generic.data.files.PropertyObject.Property;

public class PropertyException extends Exception {
	private final Property<?> property;

	/**
	 * Gets the {@link Property} associated with this exception, or
	 * <code>null</code> if none could be associated.
	 * 
	 * @return The {@link Property} associated with the exception or
	 *         <code>null</code>.
	 */
	public Property<?> getProperty() {
		return property;
	}

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	public PropertyException(Property<?> property) {
		this.property = property;
	}

	public PropertyException(String message, Property<?> property) {
		super(message);
		this.property = property;
	}

	public PropertyException(Throwable cause, Property<?> property) {
		super(cause);
		this.property = property;
	}

	public PropertyException(String message, Throwable cause, Property<?> property) {
		super(message, cause);
		this.property = property;
	}
}
