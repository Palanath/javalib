package pala.libs.generic.ml.ai.neuralnets.api.scalarfunctions;

import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;
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
 * layer in a network. Additionally, for some scalar functions (e.g.
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
 * done faster by using the function evaluation for the derivative evaluation.
 * With other types of functions, such is necessary to compute the derivative.
 * Layers made off of this type do not allow for this. Instead,
 * {@link ScalarComputation} needs to be extended to make use of the
 * {@link Container} that lets callers store intermediary values.
 * </p>
 * 
 * @author Palanath
 *
 */
public interface ScalarFunction {
	double eval(double input);

	double derivative(double input);
}
