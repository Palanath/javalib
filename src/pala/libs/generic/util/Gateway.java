package pala.libs.generic.util;

import java.util.function.Function;

public interface Gateway<F, T> {
	default Function<F, T> from() {
		return this::to;
	}

	F from(T value);

	default Gateway<T, F> inverse() {
		return new Gateway<T, F>() {

			@Override
			public T from(final F value) {
				return Gateway.this.to(value);
			}

			@Override
			public F to(final T value) {
				return Gateway.this.from(value);
			}
		};
	}

	default Function<T, F> to() {
		return this::from;
	}

	T to(F value);

	static <F, T> Gateway<F, T> from(Function<? super T, ? extends F> from, Function<? super F, ? extends T> to) {
		return new Gateway<F, T>() {

			@Override
			public F from(T value) {
				return from.apply(value);
			}

			@Override
			public T to(F value) {
				return to.apply(value);
			}
		};
	}
}
