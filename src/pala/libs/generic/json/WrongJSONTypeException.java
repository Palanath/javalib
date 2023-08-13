package pala.libs.generic.json;

/**
 * Occurs when a JSON value of the wrong type is provided to
 * {@link JSONLoadable#load(JSONValue)}. For example, if the
 * {@link JSONLoadable#load(JSONValue)} method expects a {@link JSONString} and
 * a {@link JSONArray} is provided instead, an instance of this exception will
 * be thrown.
 * 
 * @author Palanath
 *
 */
public class WrongJSONTypeException extends JSONLoadException {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	public WrongJSONTypeException(JSONValue json) {
		super(json);
	}

	public WrongJSONTypeException(String message, JSONValue json) {
		super(message, json);
	}

	public WrongJSONTypeException(String message, Throwable cause, JSONValue json) {
		super(message, cause, json);
	}

	public WrongJSONTypeException(Throwable cause, JSONValue json) {
		super(cause, json);
	}

}
