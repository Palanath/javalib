package pala.libs.generic.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import pala.libs.generic.streams.CharacterStream;

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

	/**
	 * <p>
	 * Restores this {@link JSONLoadable} from the provided {@link InputStream}.
	 * This method creates a new {@link InputStreamReader} from the provided
	 * {@link InputStream} and reads the next {@link JSONValue} from it. The
	 * {@link JSONValue} is provided to a call to {@link #fromJSON(JSONValue)}.
	 * </p>
	 * <p>
	 * The provided {@link InputStream} is not closed.
	 * </p>
	 * 
	 * @param in The {@link InputStream} to read the {@link JSONValue} from.
	 * @throws IOException       If an {@link IOException} occurs while reading from
	 *                           the {@link InputStream}.
	 * @throws JSONLoadException If a {@link JSONLoadException} occurs while
	 *                           executing {@link #fromJSON(JSONValue)}.
	 */
	default void load(InputStream in) throws IOException, JSONLoadException {
		load(new InputStreamReader(in));
	}

	default void load(Reader in) throws IOException, JSONLoadException {
		fromJSON(new JSONParser().parse(CharacterStream.from(in)));
	}

	default void load(File file) throws IOException, JSONLoadException {
		try (FileInputStream fis = new FileInputStream(file)) {
			load(fis);
		}
	}

	default void load(String json) throws JSONLoadException {
		fromJSON(new JSONParser().parse(CharacterStream.from(json)));
	}
}
