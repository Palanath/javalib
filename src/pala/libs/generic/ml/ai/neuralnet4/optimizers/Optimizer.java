package pala.libs.generic.ml.ai.neuralnet4.optimizers;

import java.util.Iterator;

import pala.libs.generic.JavaTools;
import pala.libs.generic.ml.ai.neuralnet4.api.Computation;
import pala.libs.generic.ml.ai.neuralnet4.api.LossFunction;
import pala.libs.generic.util.Pair;

public abstract class Optimizer {
	private LossFunction lossFunction;

	public LossFunction getLossFunction() {
		return lossFunction;
	}

	public void setLossFunction(LossFunction lossFunction) {
		this.lossFunction = lossFunction;
	}

	public Optimizer(LossFunction lossFunction) {
		this.lossFunction = lossFunction;
	}

	/**
	 * <p>
	 * Optimizes the provided {@link Computation} using the provided training
	 * samples. Each {@link Pair} contains a <code>double</code> correct answer
	 * array as its first value and a <code>double</code> input array as its second
	 * value. The correct answer array should be the same size as the output size of
	 * the {@link Computation} and the input array should be the same size as the
	 * input size of the {@link Computation}.
	 * </p>
	 * 
	 * @param networkToOptimize The {@link Computation} to optimize.
	 * @param labeledSamples    An array of samples to use for training.
	 */
	public final void optimize(Computation networkToOptimize,
			@SuppressWarnings("unchecked") Pair<double[], double[]>... labeledSamples) {
		optimize(networkToOptimize, JavaTools.iterator(labeledSamples));
	}

	public abstract void optimize(Computation networkToOptimze,
			Iterator<? extends Pair<? extends double[], ? extends double[]>> labeledSampleGenerator);

	public final void optimize(int iterations, Computation networkToOptimze,
			Iterator<? extends Pair<? extends double[], ? extends double[]>> labeledSampleGenerator) {
		for (int i = 0; i < iterations; i++)
			optimize(networkToOptimze, labeledSampleGenerator);
	}

	/**
	 * Invokes {@link #optimize(Computation, Pair...)} <code>iterations</code>
	 * times.
	 * 
	 * @param iterations        The number of times to invoke
	 *                          {@link #optimize(Computation, Pair...)}.
	 * @param networkToOptimize The {@link Computation} to optimize.
	 * @param labeledSamples    The array of samples to use for training.
	 */
	@SafeVarargs
	public final void optimize(int iterations, Computation networkToOptimize,
			Pair<double[], double[]>... labeledSamples) {
		for (int i = 0; i < iterations; i++)
			optimize(networkToOptimize, labeledSamples);
	}

}
