package pala.libs.generic.ml.ai.neuralnet4.optimizers;

import pala.libs.generic.ml.ai.neuralnet4.api.Computation;
import pala.libs.generic.ml.ai.neuralnet4.api.LossFunction;
import pala.libs.generic.util.Pair;

/**
 * The most trivial implementation of a {@link GradientDescent} optimizer. This
 * optimizer takes in
 * 
 * @author Palanath
 *
 */
public class GradientDescent extends Optimizer {

	private double learningRate;

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public GradientDescent(LossFunction lossFunction, double learningRate) {
		super(lossFunction);
		this.learningRate = learningRate;
	}

	@SafeVarargs
	@Override
	public final void optimize(Computation networkToOptimize, Pair<double[], double[]>... labeledSamples) {
		for (Pair<double[], double[]> pair : labeledSamples)
			networkToOptimize.calculateWeightGrads(getLossFunction(), pair.first, pair.second).forEach(a -> {
				for (int i = 0; i < a.first.length; i++)
					a.first[i] -= learningRate * a.second[i];
			});
	}

}
