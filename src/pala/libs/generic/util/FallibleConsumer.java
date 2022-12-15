package pala.libs.generic.util;

public interface FallibleConsumer<T> {
	void consume(T input) throws Exception;
}
