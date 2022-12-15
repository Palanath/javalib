package pala.libs.generic.parsing;

public abstract class BufferedParser<T> implements Parser<T> {

	private boolean cached;
	private T cache;

	@Override
	public final T next() {
		if (cached) {
			cached = false;
			return cache;
		}
		return read();
	}

	@Override
	public final T peek() {
		if (cached)
			return cache;
		cached = true;
		return cache = read();
	}

	protected abstract T read();

}
