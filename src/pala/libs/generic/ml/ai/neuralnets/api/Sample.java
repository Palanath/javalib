package pala.libs.generic.ml.ai.neuralnets.api;

import pala.libs.generic.util.Pair;

public class Sample {
	private final double[] answer, inputs;

	public Sample(double[] answer, double... inputs) {
		this.answer = answer;
		this.inputs = inputs;
	}

	public Sample(Pair<? extends double[], ? extends double[]> sample) {
		this(sample.first, (double[]) sample.second);
	}

	/**
	 * The correct answer for this training {@link Sample}. This is what the network
	 * <i>should</i> output.
	 * 
	 * @return The correct answer for this {@link Sample}.
	 */
	public double[] getAnswer() {
		return answer;
	}

	/**
	 * The inputs for the network of this training {@link Sample}. This is often
	 * features of what is being predicted or features to recognize.
	 * 
	 * @return The inputs.
	 */
	public double[] getInputs() {
		return inputs;
	}

}
