package pala.libs.generic.streams;

import java.util.function.Function;

public interface PeekableCharacterStream extends CharacterStream {
	static PeekableCharacterStream chain(final PeekableCharacterStream first, final PeekableCharacterStream second) {
		return new PeekableCharacterStream() {

			@Override
			public int next() {
				return (first.peek() >= 0 ? first : second).next();
			}

			@Override
			public int peek() {
				return (first.peek() >= 0 ? first : second).peek();
			}
		};
	}

	/**
	 * Returns a {@link PeekableCharacterStream} from the provided
	 * {@link CharacterStream}. If the provided {@link CharacterStream} is a
	 * {@link PeekableCharacterStream} already, it is returned. If not, a
	 * {@link PeekableCharacterStream} is made on top of the provided
	 * {@link CharacterStream}. The {@link PeekableCharacterStream} performs
	 * buffering to provide peeking functionality.
	 *
	 * @param parser The {@link CharacterStream} to build the
	 *               {@link PeekableCharacterStream} from.
	 * @return The {@link PeekableCharacterStream}.
	 */
	static PeekableCharacterStream from(final CharacterStream parser) {
		return parser instanceof PeekableCharacterStream ? (PeekableCharacterStream) parser
				: new PeekableCharacterStream() {
					private int peeked = -2;

					@Override
					public int next() {
						if (peeked == -2)
							return parser.next();
						final int p = peeked;
						peeked = -2;
						return p;

					}

					@Override
					public int peek() {
						return peeked == -2 ? peeked = parser.next() : peeked;
					}

					@Override
					public Character peekChar() {
						final int p = peek();
						return p == -1 ? null : (char) p;

					}
				};
	}

	/**
	 * Peeks the next {@link Character} and passes it to the provided
	 * {@link Function}. If the function returns <code>true</code>, this calls
	 * {@link #nextChar()} and collects the returned {@link Character}. If the
	 * {@link Function} returns <code>false</code>, this method returns the
	 * {@link StringBuilder} of collected characters so far. This method will finish
	 * upon the end of input.
	 *
	 * @param con The condition checked for each character.
	 * @return The collected {@link Character}s as a {@link StringBuilder}.
	 */
	default StringBuilder collect(final Function<Character, Boolean> con) {
		final StringBuilder b = new StringBuilder();
		while (peek() != -1 && con.apply(peekChar()))
			b.append(nextChar());
		return b;
	}

	/**
	 * <p>
	 * {@link #collect(Function) Collects} as much of the specified text possbile,
	 * in the same order as provided, until a non-matching character is reached.
	 * Returns the result.
	 * </p>
	 * <p>
	 * If the stream contains "excellent" and the argument provided is "excelling,"
	 * this method will return "excell" and the stream will be in such a state that
	 * a call to {@link #next()} will return the <code>e</code> after
	 * <code>excell</code> in <code>excellent</code>. The characters remaining in
	 * this stream will simply be <code>ent</code>.
	 * </p>
	 *
	 * @param text The text to parse out.
	 * @return A {@link StringBuilder} containing the parsed text.
	 */
	default StringBuilder collect(final String text) {
		return collect(new Function<Character, Boolean>() {
			int counter;

			@Override
			public Boolean apply(final Character a) {
				return counter < text.length() && a == text.charAt(counter++);
			}
		});
	}

	/**
	 * Reads over and stores text until the specified string is found. Everything up
	 * until the beginning of the specified string is returned. The stream leaves
	 * off such that {@link #next()} returns the character immediately after the
	 * provided string. If the string is not found, the entire, remaining input is
	 * parsed and returned.
	 *
	 * @param text The string to end parsing at.
	 * @return The parsed text.
	 */
	default String collectTo(final String text) {
		if (text.isEmpty())
			return "";
		final StringBuilder sb = new StringBuilder();
		PeekableCharacterStream pcs = this;
		NEXT_CHAR: while (pcs.peek() >= 0)
			if (pcs.peek() == text.charAt(0)) {
				final char piv = (char) pcs.next();

				for (int i = 1; i < text.length(); i++)
					if (pcs.peek() < 0)
						return sb.append(text.substring(0, i)).toString();
					else if (pcs.peek() != text.charAt(i)) {
						pcs = chain(from(CharacterStream.from(text.substring(1, i))), pcs);
						sb.append(piv);
						continue NEXT_CHAR;
					} else
						pcs.next();
				return sb.toString();
			} else
				sb.append(pcs.nextChar());
		return sb.toString();
	}

	/**
	 * <p>
	 * This method calls {@link #collectTo(String)} twice, once with the first
	 * argument to this method and again with the second argument, and returns the
	 * result of the second call.
	 * </p>
	 * <p>
	 * This method works by parsing all the text up to and including the specified
	 * <code>openingTag</code> and ignoring it. It then parses all the text after
	 * that up to the specified <code>closingTag</code> and stores it, then parses
	 * out the closing tag itself. The stored text (between the opening and closing
	 * tag) is returned, and the {@link PeekableCharacterStream} leaves off right
	 * after the specified <code>closingTag</code>.
	 * </p>
	 * <p>
	 * If the opening tag is not contained in the stream, the result is an empty
	 * string and the stream is fully parsed. Otherwise, if the closing tag is not
	 * contained in the stream, the result is everything after the opening tag, and
	 * the stream is fully parsed.
	 * </p>
	 *
	 * @param openingTag The opening tag.
	 * @param closingTag The closing tag.
	 * @return The text found between the opening and closing tags.
	 */
	default String parseBetween(final String openingTag, final String closingTag) {
		collectTo(openingTag);
		return collectTo(closingTag);
	}

	int peek();

	default Character peekChar() {
		final int peekch = peek();
		return peekch < 0 ? null : (Character) (char) peekch;
	}

	/**
	 * Calls {@link #collect(String)} and returns <code>true</code> if all of the
	 * parsed text was contained in the stream (and was parsed out). Returns
	 * <code>true</code> if the provided argument's length is the same as the result
	 * of {@link #collect(String)}.
	 *
	 * @param text
	 * @return
	 */
	default boolean scan(final String text) {
		return collect(text).length() == text.length();
	}

	default void skipWhitespace() {
		while (peek() >= 0 && Character.isWhitespace(peek()))
			next();
	}
}
