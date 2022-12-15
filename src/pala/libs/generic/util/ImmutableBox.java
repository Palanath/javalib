package pala.libs.generic.util;

public class ImmutableBox<T> {
	public final T value;

	public ImmutableBox(final T value) {
		this.value = value;
	}
}
