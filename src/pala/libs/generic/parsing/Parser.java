package pala.libs.generic.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface Parser<T> {
	default List<T> collect(final Function<T, Boolean> cond) {
		final List<T> el = new ArrayList<>();
		while (peek() != null && cond.apply(peek()))
			el.add(next());
		return el;
	}

	/**
	 * Returns the next value and steps forward.
	 *
	 * @return The next value.
	 */
	T next();

	default List<T> parseAll() {
		final List<T> el = new ArrayList<>();
		while (peek() != null)
			el.add(next());
		return el;
	}

	/**
	 * Returns the next value without stepping forward. Repeated calls to this
	 * method will return the same value.
	 *
	 * @return Returns the next value.
	 */
	T peek();
}
