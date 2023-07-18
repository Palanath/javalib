package pala.libs.generic.ml.ai.neuralnets.optimizers;

import java.util.Iterator;
import java.util.function.BiFunction;

import pala.libs.generic.JavaTools;
import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.api.Node;
import pala.libs.generic.ml.ai.neuralnets.api.Sample;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;
import pala.libs.generic.util.Pair;

public abstract class Optimizer {
	private LossFunction lossFunction;
	private BiFunction<Node, Integer, Double> zeroWeightAdjuster;

	public BiFunction<Node, Integer, Double> getZeroWeightAdjuster() {
		return zeroWeightAdjuster;
	}

	public void setZeroWeightAdjuster(BiFunction<Node, Integer, Double> zeroWeightAdjuster) {
		this.zeroWeightAdjuster = zeroWeightAdjuster;
	}

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
	public void optimize(Computation networkToOptimize, Sample... labeledSamples) {
		optimize(networkToOptimize, JavaTools.iterator(labeledSamples));
	}

	public abstract void optimize(Computation networkToOptimze, Iterator<? extends Sample> labeledSampleGenerator);

	public final void optimize(int iterations, Computation networkToOptimze,
			Iterator<? extends Sample> labeledSampleGenerator) {
		for (int i = 0; i < iterations; i++)
			optimize(networkToOptimze, labeledSampleGenerator);
	}

	public final void optimize(int iterations, Computation networkToOptimize,
			Iterable<? extends Sample> labeledSamples) {
		optimize(iterations, networkToOptimize, labeledSamples.iterator());
	}

	public final void optimize(Computation networkToOptimize, Iterable<? extends Sample> labeledSamples) {
		optimize(networkToOptimize, labeledSamples.iterator());
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
	public final void optimize(int iterations, Computation networkToOptimize, Sample... labeledSamples) {
		for (int i = 0; i < iterations; i++)
			optimize(networkToOptimize, labeledSamples);
	}

	public static void adjustWeights(WeightGradStorage grads, double learningRate) {
		for (Pair<double[], double[]> p : grads)
			for (int i = 0; i < p.first.length; i++)
				p.first[i] -= learningRate * p.second[i];

	}

	void subtractGrads(WeightGradStorage weightGrads, double learningRate) {
		for (Pair<Node, double[]> v : weightGrads.all())
			for (int i = 0; i < v.first.weights(); i++)
				if (zeroWeightAdjuster != null & (v.first.getBackingWeights()[i] -= learningRate * v.second[i]) == 0)
					v.first.getBackingWeights()[i] = zeroWeightAdjuster.apply(v.first, i);
	}

}
