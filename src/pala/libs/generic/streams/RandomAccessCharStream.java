package pala.libs.generic.streams;

public interface RandomAccessCharStream extends PeekableCharacterStream, Cloneable {

	interface Mark {
		void jumpTo();
	}

	static RandomAccessCharStream from(final String input) {
		class RandomAccessCharStreamImpl implements RandomAccessCharStream {
			class Mark implements RandomAccessCharStream.Mark {
				private final int mark = currentPoint;

				@Override
				public void jumpTo() {
					currentPoint = mark;
				}

			}

			private int currentPoint;

			public RandomAccessCharStreamImpl() {
			}

			public RandomAccessCharStreamImpl(final int currentPoint) {
				this.currentPoint = currentPoint;
			}

			@Override
			public RandomAccessCharStream clone() {
				return new RandomAccessCharStreamImpl(currentPoint);
			}

			@Override
			public int codepoint(final int index) {
				return input.codePointAt(index);
			}

			@Override
			public Mark mark() {
				return new Mark();
			}

			@Override
			public int next() {
				return input.codePointAt(currentPoint++);
			}

			@Override
			public int peek() {
				return input.codePointAt(currentPoint);
			}

			@Override
			public Character peekChar() {
				return peek() < 0 ? null : (char) peek();
			}
		}
		return new RandomAccessCharStreamImpl();
	}

	RandomAccessCharStream clone();

	int codepoint(int index);

	Mark mark();

}
