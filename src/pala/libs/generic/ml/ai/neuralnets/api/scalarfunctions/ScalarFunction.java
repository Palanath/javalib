package pala.libs.generic.ml.ai.neuralnets.api.scalarfunctions;

import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.computations.scalar.ScalarComputation;
import pala.libs.generic.ml.ai.neuralnets.computations.scalar.SigmoidComputation;

/**
 * <p>
 * A mathematical function that takes one input and has one output. Calculation
 * of its derivative can be done easily without the evaluation of the function
 * itself at that same point.
 * </p>
 * <p>
 * This type is similar to the {@link ScalarComputation} class except that it
 * represents only a mathematical function and cannot function on its own as a
 * layer in a network, (though, network layers can be made off of it. See
 * {@link #computation(int)}). Additionally, for some scalar functions (e.g.
 * {@link SigmoidComputation Sigmoid}), the derivative is a simpler function of
 * the function itself, than it is of the input:
 * </p>
 * 
 * <pre>
 * <code>S(x) = 1/(1+e^(-x))
 * S'(x) = (1/(1+e^(-x))) * (1 - 1/(1+e^(-x)))
 * S'(x) = S(x) * (1 - S(x))</code>
 * </pre>
 * 
 * <p>
 * With these types of functions, evaluating the function on some input
 * (<code>x</code>) then evaluating the derivative at that same input can be
 * done faster by using the function evaluation for the derivative calculation.
 * With even some other types of functions, such is necessary. Layers made off
 * of this type, using {@link #computation(int)}, do not implicitly do this.
 * Instead, {@link ScalarComputation} needs to be extended to make use of the
 * {@link Container} that lets callers store intermediary values, or
 * {@link #computation(int)} needs to be overridden to return a
 * {@link ScalarComputation} that does this.
 * </p>
 * 
 * @author Palanath
 *
 */
public interface ScalarFunction {
	double eval(double input);

	double derivative(double input);

	/**
	 * <p>
	 * Creates a {@link ScalarComputation} that computes this {@link ScalarFunction}
	 * at every input. The results each become an output, giving the same number of
	 * outputs as inputs.
	 * </p>
	 * <p>
	 * This function can be overridden to return a {@link ScalarComputation} which
	 * allows caching the value computed in the forward pass for use in the backward
	 * pass or otherwise.
	 * </p>
	 * 
	 * @param inputs The number of inputs in the {@link ScalarComputation}.
	 * @return The {@link ScalarComputation}.
	 */
	default ScalarComputation computation(int inputs) {
		return new ScalarComputation(inputs) {

			@Override
			public double grad(Container c) {
				return derivative(c.get());
			}

			@Override
			public double eval(Container c, double input) {
				c.set(input);
				return ScalarFunction.this.eval(input);
			}
		};
	}
}
