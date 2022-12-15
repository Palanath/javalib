package pala.libs.generic.parsers.cli;

public interface Argument {
	/**
	 * Returns the name of this {@link Argument}. If this argument is a value
	 * argument, this method returns exactly the same result as {@link #value()}. If
	 * this {@link Argument} is a key-value argument, this method returns the key of
	 * the {@link Argument}.
	 *
	 * @return This {@link Argument}'s name.
	 */
	String name();

	/**
	 * Returns the prefix of this {@link Argument}, or the empty string if there is
	 * none. Prefixes are either a <code>-</code> or <code>--</code>.
	 *
	 * @return The prefix, if any, otherwise the empty string..
	 */
	String prefix();

	/**
	 * Determines whether or not this command line argument had a prefix of either
	 * <code>-</code> or <code>--</code>.
	 *
	 * @return <code>true</code> if this argument had a prefix.
	 */
	default boolean prefixed() {
		return !"".equals(prefix());
	}

	/**
	 * <p>
	 * Returns the value of this argument. If this argument is a simple flag/value
	 * argument, (particularly in that it is not a key-value pair), then its value
	 * is just its name. Examples of these are:
	 * </p>
	 * <ul>
	 * <li><code>-test</code></li>
	 * <li><code>arg2</code></li>
	 * <li><code>--argument-three</code></li>
	 * </ul>
	 * <p>
	 * Key-map values will return their value portion. This sometimes causes
	 * confusion, because the name of a key-value pair is usually the key and the
	 * name of a simple value is usually its value.
	 * </p>
	 * <p>
	 * Other types of arguments carry different notions of value and return results
	 * from this method accordingly.
	 * </p>
	 *
	 * @return The value of this argument. All arguments have a value (although it
	 *         may be the empty {@link String} if nothing was provided in the
	 *         argument but a prefix).
	 */
	String value();
}
