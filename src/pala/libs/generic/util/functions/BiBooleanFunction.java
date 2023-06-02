package pala.libs.generic.util.functions;

import java.util.function.BiFunction;

public interface BiBooleanFunction<I1, I2> extends BiFunction<I1, I2, Boolean> {

	@Override
	default Boolean apply(I1 t, I2 u) {
		return run(t, u);
	}

	boolean run(I1 input1, I2 input2);

}
