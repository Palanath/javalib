package pala.libs.generic.data.files;

import pala.libs.generic.data.files.PropertyObject.Property;

/**
 * Thrown when a {@link Property} attempts to load JSON data but encounters and
 * does not allow <code>null</code>.
 * 
 * @author Palanath
 *
 */
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

	public NullJSONException(String message, NullPointerException cause, Property<?> property) {
		super(message, cause, property);
	}

	public NullJSONException(NullPointerException cause, Property<?> property) {
		super(cause, property);
	}

	public NullJSONException(NullPointerException cause) {
		this(cause, null);
	}

}
