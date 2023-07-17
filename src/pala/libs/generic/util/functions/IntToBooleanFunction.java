package pala.libs.generic.util.functions;

import java.util.function.IntFunction;
import java.util.function.Predicate;

public interface IntToBooleanFunction extends IntFunction<Boolean>, Predicate<Integer> {
	@Override
	default Boolean apply(int value) {
		return run(value);
	}

	@Override
	default boolean test(Integer t) {
		return run(t);
	}

	boolean run(int value);
}
