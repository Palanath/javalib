package pala.libs.generic.util.functions;

import java.util.function.BiFunction;

public interface BiDoubleFunction<F, S> extends BiFunction<F, S, Double> {
	@Override
	default Double apply(F t, S u) {
		return run(t, u);
	}
	
	double run(F first, S second);
}
