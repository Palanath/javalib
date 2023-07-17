package pala.libs.generic.util.functions;

import java.util.function.Function;
import java.util.function.Predicate;

public interface CharToBooleanFunction extends Predicate<Character>, Function<Character, Boolean> {
	@Override
	default Boolean apply(Character t) {
		return run(t);
	}

	@Override
	default boolean test(Character t) {
		return run(t);
	}

	boolean run(char value);
}
