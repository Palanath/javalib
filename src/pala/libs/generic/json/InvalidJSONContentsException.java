package pala.libs.generic.json;

/**
 * <p>
 * Occurs when the appropriate type of JSON value was provided to
 * {@link JSONLoadable#load(JSONValue)}, but when that {@link JSONValue} did not
 * contain the appropriate information, or was mal-structured, or otherwise
 * could not be used to load the {@link JSONLoadable}.
 * </p>
 * 
 * @author Palanath
 *
 */
public class InvalidJSONContentsException extends JSONLoadException {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	public InvalidJSONContentsException(JSONValue json) {
		super(json);
	}

	public InvalidJSONContentsException(String message, JSONValue json) {
		super(message, json);
	}

	public InvalidJSONContentsException(String message, Throwable cause, JSONValue json) {
		super(message, cause, json);
	}

	public InvalidJSONContentsException(Throwable cause, JSONValue json) {
		super(cause, json);
	}

}
