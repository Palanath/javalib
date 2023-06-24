package pala.libs.generic.ml.ai.neuralnet4.optimizers;

import java.util.Iterator;

import pala.libs.generic.ml.ai.neuralnet4.api.Computation;
import pala.libs.generic.ml.ai.neuralnet4.api.LossFunction;
import pala.libs.generic.util.Pair;

/**
 * The most trivial implementation of a {@link GradientDescentOptimizer}
 * optimizer. This optimizer takes a {@link #learningRate} parameter which the
 * gradient is multiplied by before being subtracted from the weights in an
 * update step.
 * 
 * @author Palanath
 *
 */
public class GradientDescentOptimizer extends Optimizer {

	private double learningRate;

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public GradientDescentOptimizer(LossFunction lossFunction, double learningRate) {
		super(lossFunction);
		this.learningRate = learningRate;
	}

	@Override
	public void optimize(Computation networkToOptimize,
			Iterator<? extends Pair<? extends double[], ? extends double[]>> labeledSamples) {
		while (labeledSamples.hasNext()) {
			Pair<? extends double[], ? extends double[]> pair = labeledSamples.next();
			networkToOptimize.calculateWeightGrads(getLossFunction(), pair.first, (double[]) pair.second).forEach(a -> {
				for (int i = 0; i < a.first.length; i++)
					a.first[i] -= learningRate * a.second[i];
			});
		}
	}

}
