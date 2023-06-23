package pala.libs.generic.ml.ai.neuralnet4;

public interface LossFunction {
	/**
	 * @return The number of inputs that this {@link LossFunction} accepts.
	 */
	int inputs();

	/**
	 * Evaluates this loss function on the provided inputs.
	 * 
	 * @param c             The {@link Container} to store the forward pass
	 *                      information in.
	 * @param prediction    A sample, prediction, or guess to compare against the
	 *                      correct answer. The loss, which indicates how far off
	 *                      this prediction was from the correct answer, is what is
	 *                      returned.
	 * @param correctAnswer The correct answer to compare the prediction against.
	 * @return The loss.
	 */
	double evaluateLoss(Container c, double[] correctAnswer, double... prediction);

	default double evalLoss(double[] correctAnswer, double... inputs) {
		return evaluateLoss(Container.DUMMY, inputs);
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
