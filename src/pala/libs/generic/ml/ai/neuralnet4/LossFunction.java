package pala.libs.generic.ml.ai.neuralnet4;

public interface LossFunction {
	/**
	 * @return The number of inputs that this {@link LossFunction} accepts.
	 */
	int inputs();

	/**
	 * Evaluates this loss function on the provided inputs.
	 * 
	 * @param c      The {@link Container} to store the forward pass information in.
	 * @param inputs The inputs to pass to this function.
	 * @return The loss.
	 */
	double evaluate(Container c, double... inputs);

	default double eval(double... inputs) {
		return evaluate(Container.DUMMY, inputs);
	}

	/**
	 * Calculates the gradient of each of the inputs with respect to the loss of the
	 * execution represented by the {@link Container}.
	 * 
	 * @param c The {@link Container}, containing the state saved during a forward
	 *          pass.
	 * @return An array containing the derivative of the loss (output of this
	 *         function) with respect to each of the inputs to this function.
	 */
	double[] grad(Container c);
}
