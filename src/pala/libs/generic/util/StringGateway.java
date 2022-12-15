package pala.libs.generic.util;

public interface StringGateway<T> extends Gateway<String, T> {
	static StringGateway<String> string() {
		return a -> a;
	}

	@Override
	default String from(final T value) {
		return value.toString();
	}
}
