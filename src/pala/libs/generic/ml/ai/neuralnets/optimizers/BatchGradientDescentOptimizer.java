package pala.libs.generic.ml.ai.neuralnets.optimizers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.api.Node;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;
import pala.libs.generic.util.Pair;

public class BatchGradientDescentOptimizer extends Optimizer {

	private double learningRate;

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public BatchGradientDescentOptimizer(LossFunction lossFunction, double learningRate) {
		super(lossFunction);
		this.learningRate = learningRate;
	}

	@Override
	public void optimize(Computation networkToOptimze,
			Iterator<? extends Pair<? extends double[], ? extends double[]>> labeledSampleGenerator) {
		List<WeightGradStorage> grads = new ArrayList<>();
		while (labeledSampleGenerator.hasNext()) {
			Pair<? extends double[], ? extends double[]> p = labeledSampleGenerator.next();
			grads.add(networkToOptimze.calculateWeightGrads(getLossFunction(), p.first, (double[]) p.second));
		}

		// Average weight grad storages
		WeightGradStorage wgs = grads.get(0).clone();
		for (Pair<Node, double[]> ngs : wgs.all()) {
			for (int i = 1; i < grads.size(); i++)
				for (int j = 0; j < ngs.second.length; j++)
					ngs.second[j] += grads.get(i).get(ngs.first)[j];// Sum up all weight grads
			for (int i = 0; i < ngs.second.length; i++)
				ngs.second[i] /= grads.size(); // Divide by total number.
		}
		subtractGrads(wgs, learningRate);
	}

}
