package pala.libs.generic.ml.ai.neuralnets.api;

/**
 * A {@link Computation} with only one output, but (generally) more than one
 * input.
 * 
 * @author Palanath
 *
 */
public interface VectorComputation extends Computation {
	@Override
	default int outputs() {
		return 1;
	}

	default double evalToScalar(double... inputs) {
		return eval(inputs)[0];
	}

	default double evaluateToScalar(Container c, double... inputs) {
		return evaluate(c, inputs)[0];
	}
}
