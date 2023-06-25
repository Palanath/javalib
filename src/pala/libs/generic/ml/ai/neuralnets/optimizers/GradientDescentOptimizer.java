package pala.libs.generic.ml.ai.neuralnets.optimizers;

import java.util.Iterator;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.api.Sample;

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
	public void optimize(Computation networkToOptimize, Iterator<? extends Sample> labeledSamples) {
		while (labeledSamples.hasNext()) {
			networkToOptimize.calculateWeightGrads(getLossFunction(), labeledSamples.next()).forEach(a -> {
				for (int i = 0; i < a.first.length; i++)
					a.first[i] -= learningRate * a.second[i];
			});
		}
	}

}
