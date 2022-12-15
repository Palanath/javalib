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
}
