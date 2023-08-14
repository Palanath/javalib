package pala.libs.generic.json;

public interface JSONLoadable {
	/**
	 * <p>
	 * Attempts to restore the state of this object from the provided
	 * {@link JSONValue}.
	 * </p>
	 * <p>
	 * If the provided {@link JSONValue} is not of the appropriate JSON type, this
	 * method throws a {@link WrongJSONTypeException}. If the provided
	 * {@link JSONValue} is of the appropriate JSON type, but does not represent an
	 * instance of this object (or otherwise does not contain the appropriate
	 * information to restore this object) then this method throws an
	 * {@link InvalidJSONContentsException}.
	 * </p>
	 * <p>
	 * Subclasses may declare other exception types, or more fine-grained exception
	 * types, in the throws clause of their implementations of this method.
	 * </p>
	 * 
	 * @param json The {@link JSONValue} to attempt to restore from.
	 * @throws JSONLoadException If an exception occurs while
	 */
	void fromJSON(JSONValue json) throws JSONLoadException;
}
