package pala.libs.generic.streams;

import java.util.Iterator;

/**
 * <p>
 * Streams are similar to {@link Iterator}s. They move over elements (items) of
 * some similar type in a sequence (whose size is not necessarily known).
 * </p>
 *
 * <p>
 * Streams start <i>before</i> the first element. The element the stream is
 * "currently on" is the element that was last returned by a call to
 * {@link #next()}, unless otherwise specified.
 * </p>
 *
 * @author Palanath
 *
 * @param <I> The type of item in the stream.
 */
public interface Stream<I> {
	/**
	 * Moves to the next item and returns it.
	 *
	 * @return The next item in the {@link Stream}.
	 */
	I next();
}
