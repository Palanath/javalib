package pala.libs.generic.ml.ai.neuralnet;

/**
 * Represents a function whose derivative can be evaluated as well. This type is
 * used by the neural network framework to allow arbitrary functions to be used
 * for making neural networks.
 * 
 * @author Palanath
 *
 */
public interface DifferentiableFunction {
	/**
	 * Evaluates this function on the provided point. The size of the provided
	 * vector should match the input size of this function.
	 * 
	 * @param input The point to evaluate the function on.
	 * @return The result of the evaluation.
	 */
	double evaluate(double... input);

	/**
	 * Evaluates this function's derivative on the provided point. The size of the
	 * provided vector should match the input size of this function.
	 * 
	 * @param input The point to evaluate the derivative on.
	 * @return The derivative.
	 */
	double evaluateDerivative(double... input);
}
