package pala.libs.generic.strings.matching;

import java.util.function.Function;

/**
 * @author Palanath
 */
public interface Matching {

	static Matching build(final String matching) {
		return text -> text.startsWith(matching) ? text.substring(matching.length()) : text;
	}

	static Matching build(final String... matchings) {
		return text -> {
			int m = 0;
			for (final String s : matchings)
				if (s.length() > m && text.startsWith(s.toLowerCase()))
					m = s.length();
			return text.substring(m);
		};
	}

	static Matching buildLazy(final String... matchings) {
		return text -> {
			for (final String s : matchings)
				if (text.startsWith(s))
					return text.substring(s.length());
			return text;
		};
	}

	/**
	 * Returns <code>true</code> if the character matches this matching,
	 * <code>false</code> otherwise.
	 *
	 * @param filter The filter to use to check if each {@link Character} in the
	 *               string to be matched matches.
	 * @return The new {@link Matching}.
	 */
	static Matching filter(final Function<Character, Boolean> filter) {
		return text -> {
			for (int i = 0; i < text.length(); i++)
				if (!filter.apply(text.charAt(i)))
					return text.substring(i);
			return "";
		};
	}

	static Matching identifier() {
		return text -> {
			if (text.isEmpty() || (!Character.isLetter(text.charAt(0)) && (text.charAt(0) != '_')))
				return text;
			while (!text.isEmpty() && (Character.isLetter(text.charAt(0)) || Character.isDigit(text.charAt(0))
					|| text.charAt(0) == '_'))
				text = text.substring(1);
			return text;
		};
	}

	/**
	 * <p>
	 * Returns a {@link Matching} that represents an inclusive OR between the
	 * specified {@link String}s, disregarding capitalization. The results of calls
	 * to {@link #match(String)} on the resulting {@link Matching} are the longest
	 * match possible given the {@link String}s specified when calling this method
	 * (the {@link Matching} is eager).
	 * </p>
	 * <p>
	 * The resulting {@link Matching} favors the arguments to a call to this method
	 * that match more of the input given to the {@link Matching} than others, in
	 * that if a {@link Matching} is built with this method using the arguments
	 * <code>a</code>, and <code>abc</code>, in that order, then the string
	 * <code>abcde</code> would render <code>de</code> when checked against the
	 * resulting {@link Matching}.
	 * <p>
	 *
	 * @param matchings The {@link String}s that the resulting {@link Matching}
	 *                  should match against.
	 * @return An inclusive-OR, eager {@link Matching}.
	 */
	static Matching ignoreCase(final String... matchings) {
		return text -> {
			final String text2 = text.toLowerCase();
			int m = 0;
			for (final String s : matchings)
				if (s.length() > m && text2.startsWith(s.toLowerCase()))
					m = s.length();
			return text.substring(m);
		};
	}

	static Matching ignoreCase(final String matching) {
		return text -> text.toLowerCase().startsWith(matching.toLowerCase()) ? text.substring(matching.length()) : text;
	}

	static Matching ignoreCaseLazy(final String... matchings) {
		return text -> {
			final String text2 = text.toLowerCase();
			for (final String s : matchings)
				if (text2.startsWith(s))
					return text.substring(s.length());
			return text;
		};
	}

	static Matching letters() {
		return filter(Character::isLetter);
	}

	static Matching numbers() {
		return filter(Character::isDigit);
	}

	/**
	 * Returns a matching which optionally matches <code>thiz</code>, but
	 * necessarily matches <code>then</code>.
	 *
	 * @param thiz The "possible" matching.
	 * @param then The following, required matching.
	 * @return A compound {@link Matching} built off of the two parameters.
	 */
	static Matching possibly(final Matching thiz, final Matching then) {
		return text -> {
			// This method can encounter "problems" when thiz and then are the same. Docs
			// are needed.

			final String thisMatch = thiz.match(text);
			final String furtherMatch = then.match(thisMatch);

			if (furtherMatch.length() != thisMatch.length())
				// If then.match() consumed something, we don't care if thiz.match() did,
				// because thiz is optional.
				return furtherMatch;
			if (thisMatch.length() != text.length())
				// If then wasn't matched however, we'll need to see if it can be matched when
				// thiz isn't matched in the first place. We don't want our optional thiz
				// element consuming text that the necessary then element would otherwise
				// consume.
				//
				// This situation only applies when thisMatch.length() != text.length().
				return then.match(text);
			return text;
		};
	}

	static Matching whitespace() {
		return filter(Character::isWhitespace);
	}

	/**
	 * Matches, exactly, against the given text.
	 *
	 * @param text The text to match against.
	 * @return <code>true</code> if the given text is an exact match,
	 *         <code>false</code> otherwise.
	 */
	default boolean fullyMatches(final String text) {
		return match(text).isEmpty();
	}

	/**
	 * <p>
	 * Returns the text that remains after the given {@link String} is matched
	 * against this object. If the text does not match this object, the full text is
	 * returned. This method always returns a {@link String} object.
	 * </p>
	 * <p>
	 * For example, consider the {@link Matching}:
	 *
	 * <pre>
	 * <code>Matching m = Matching.build("p").then(Matching.ignoreCase("q"));</code>
	 * </pre>
	 *
	 * Following is the result of the {@link Matching} functions,
	 * {@link #match(String)}, {@link #matches(String)}, and
	 * {@link #fullyMatches(String)}, called on <code>m</code>, against various
	 * {@link String}s.
	 *
	 * <style> table, th, td { border: 1px solid black; border-collapse: collapse; }
	 * td { padding: 3px; } th { padding: 5px; } </style> <br>
	 * <br>
	 * <table style="width:100%;" border=1>
	 * <tr>
	 * <th>Input</th>
	 * <th>{@link #match(String)}</th>
	 * <th>{@link #matches(String)}</th>
	 * <th>{@link #fullyMatches(String)}</th>
	 * </tr>
	 * <tr>
	 * <td><code>"p"</code></td>
	 * <td><code>"p"</code></td>
	 * <td><code>false</code></td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>"P"</code></td>
	 * <td><code>"P"</code></td>
	 * <td><code>false</code></td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>"pX"</code></td>
	 * <td><code>"pX"</code></td>
	 * <td><code>false</code></td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>"pq"</code></td>
	 * <td><code>""</code></td>
	 * <td><code>true</code></td>
	 * <td><code>true</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>"pqX"</code></td>
	 * <td><code>"X"</code></td>
	 * <td><code>true</code></td>
	 * <td><code>false</code></td>
	 * </tr>
	 * </table>
	 * <br>
	 *
	 * @param text The {@link String} to match this object against.
	 * @return The remaining text that didn't match this object.
	 */
	String match(String text);

	/**
	 * Returns <code>true</code> if the given {@link String} starts with this
	 * {@link Matching}. Every necessary part of the matching must be fulfilled in
	 * the given {@link String} for this method to return <code>true</code>, but
	 * there can be excessive characters after
	 *
	 * @param text The text to match against this object.
	 * @return <code>text.length() != this.match().length()</code>
	 */
	default boolean matches(final String text) {
		return text.length() != match(text).length();
	}

	default Matching or(final boolean ignoreCase, final String... others) {
		return or(ignoreCase ? ignoreCase(others) : build(others));
	}

	default Matching or(final Matching other) {
		return text -> {
			final String firstMatch = match(text);
			return firstMatch.length() == text.length() ? other.match(text) : firstMatch;
		};
	}

	default Matching or(final String... others) {
		return or(false, others);
	}

	default Matching possibly(final boolean ignoreCase, final String... others) {
		return possibly(ignoreCase ? ignoreCase(others) : build(others));
	}

	default Matching possibly(final Matching other) {
		return text -> {
			final String firstMatch = match(text);
			if (firstMatch.length() == text.length())// The previous text is required.
				return text;

			if (firstMatch.isEmpty())
				return "";
			final String otherMatch = other.match(firstMatch);
			return otherMatch.length() == firstMatch.length() ? firstMatch : otherMatch;
		};
	}

	default Matching possibly(final String... others) {
		return possibly(false, others);
	}

	default Matching then(final boolean ignoreCase, final String... others) {
		return then(ignoreCase ? ignoreCase(others) : build(others));
	}

	default Matching then(final Matching other) {
		return text -> {
			final String firstMatch = match(text);
			if (firstMatch.isEmpty() || firstMatch.length() == text.length())
				return text;
			final String secondMatch = other.match(firstMatch);
			return secondMatch.length() == firstMatch.length() ? text : secondMatch;
		};
	}

	default Matching then(final String... others) {
		return then(false, others);
	}

}
