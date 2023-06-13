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
	double evaluate(double... input);

	double evaluateDerivative(double... input);
}
