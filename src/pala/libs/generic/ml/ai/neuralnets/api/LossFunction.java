package pala.libs.generic.ml.ai.neuralnets.api;

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

	default double evalLoss(double[] correctAnswer, double... prediction) {
		return evaluateLoss(Container.DUMMY, correctAnswer, prediction);
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

	static LossFunction meanSquaredError(int inputs) {
		return new LossFunction() {

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public double[] grad(Container c) {
				return c.get();
			}

			@Override
			public double evaluateLoss(Container c, double[] correctAnswer, double... prediction) {
				double[] diffs = new double[inputs];
				c.set(diffs);
				double res = 0;
				for (int i = 0; i < inputs; i++) {
					diffs[i] = prediction[i] - correctAnswer[i];
					res += diffs[i] * diffs[i];
					diffs[i] *= 2d / inputs;
				}
				return res / inputs;
			}
		};
	}

	static LossFunction crossEntropy(int classes) {
		return crossEntropy(classes, 1e-9);
	}

	static LossFunction crossEntropy(int classes, double epsilon) {
		return new LossFunction() {

			@Override
			public int inputs() {
				return classes;
			}

			@Override
			public double[] grad(Container c) {
				double[] t[] = c.get(), answers = t[0], pr = t[1], res = answers.clone();
				for (int i = 0; i < res.length; i++)
					res[i] /= -(pr[i] + epsilon);
				return res;
			}

			@Override
			public double evaluateLoss(Container c, double[] correctAnswer, double... prediction) {
				assert correctAnswer.length == classes && classes == prediction.length
						: "Invalid input array sizes for cross entropy loss func.";
				c.set(new double[][] { correctAnswer, prediction });
				double sum = 0;
				for (int i = 0; i < prediction.length; i++)
					sum += correctAnswer[i] * Math.log(prediction[i]);
				return -sum;
			}
		};
	}
}
