package pala.libs.generic.util;

import java.util.function.Supplier;

public interface FallibleSupplier<E extends Throwable, S> {
	static <E extends Throwable, S> FallibleSupplier<E, S> fallible(final Supplier<? extends S> supplier) {
		return supplier::get;
	}

	S get() throws E;

	default Supplier<S> uncheck() {
		return () -> {
			try {
				return FallibleSupplier.this.get();
			} catch (final Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}

}
