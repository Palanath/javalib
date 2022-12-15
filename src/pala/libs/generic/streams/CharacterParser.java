package pala.libs.generic.streams;

import java.io.IOException;
import java.io.Reader;

public interface CharacterParser extends CharacterStream {
	static CharacterParser from(final CharacterStream stream) {
		return new CharacterParser() {
			int curr = -2;

			@Override
			public int curr() throws RuntimeException {
				return curr;
			}

			@Override
			public int next() {
				return curr = stream.next();
			}
		};
	}

	static CharacterParser from(final Reader reader) {
		return new CharacterParser() {
			int curr;

			@Override
			public int curr() throws RuntimeException {
				return curr;
			}

			@Override
			public int next() {
				try {
					return curr = reader.read();
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	/**
	 * Returns the last value returned by {@link #next()}, or throws an exception if
	 * none exists.
	 *
	 * @return The current character that this parser is on.
	 */
	int curr() throws RuntimeException;
}
