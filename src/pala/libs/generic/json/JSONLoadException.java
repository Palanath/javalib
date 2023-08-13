package pala.libs.generic.json;

/**
 * Occurs when {@link JSONLoadable#load(JSONValue)} fails due to an issue
 * related to the actual {@link JSONValue} provided. This exception is the
 * supertype of all JSON-related issues invovled in the
 * {@link JSONLoadable#load(JSONValue)} method.
 * 
 * @author Palanath
 *
 */
public class JSONLoadException extends Exception {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	private final JSONValue json;

	public JSONValue getJson() {
		return json;
	}

	public JSONLoadException(Throwable cause, JSONValue json) {
		super(cause);
		this.json = json;
	}

	public JSONLoadException(String message, Throwable cause, JSONValue json) {
		super(message, cause);
		this.json = json;
	}

	public JSONLoadException(String message, JSONValue json) {
		super(message);
		this.json = json;
	}

	public JSONLoadException(JSONValue json) {
		this.json = json;
	}

}
