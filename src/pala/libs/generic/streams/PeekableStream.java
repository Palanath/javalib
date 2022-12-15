package pala.libs.generic.streams;

/**
 * <p>
 * A stream whose next element can be "peeked." A call to {@link #peek()}
 * returns the next element in the stream without moving the stream forward.
 * </p>
 *
 * @author Palanath
 *
 * @param <I> The type of value in the {@link Stream}.
 */
public interface PeekableStream<I> extends Stream<I> {

	/**
	 * Creates a {@link PeekableCharacterStream} from any {@link Stream}. If the
	 * provided {@link Stream} is an instance of {@link PeekableStream}, it is
	 * simply returned. Otherwise, a {@link PeekableStream} that contains a
	 * 1-element buffer is created and returned.
	 *
	 * @param <I>    The type of item in the stream.
	 * @param stream The stream that the {@link PeekableStream} will invoke.
	 * @return The new {@link PeekableStream}.
	 */
	static <I> PeekableStream<I> from(final Stream<I> stream) {
		return stream instanceof PeekableStream ? (PeekableStream<I>) stream : new PeekableStream<I>() {
			private I temp;
			private boolean hasValue;

			@Override
			public I next() {
				if (hasValue) {
					hasValue = false;
					final I x = temp;
					temp = null;
					return x;
				}
				return stream.next();
			}

			@Override
			public I peek() {
				if (hasValue)
					return temp;
				hasValue = true;
				return temp = stream.next();
			}
		};
	}

	/**
	 * Peeks the next element in the stream without moving to it.
	 *
	 * @return The next element in the stream.
	 */
	I peek();
}
