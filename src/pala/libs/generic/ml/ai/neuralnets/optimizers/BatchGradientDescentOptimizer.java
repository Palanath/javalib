package pala.libs.generic.ml.ai.neuralnets.optimizers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.api.Node;
import pala.libs.generic.ml.ai.neuralnets.api.Sample;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;
import pala.libs.generic.util.Pair;

public class BatchGradientDescentOptimizer extends Optimizer {

	private double learningRate;
	/**
	 * Gets called every time one sample is completed.
	 */
	private Runnable callback;

	public Runnable getCallback() {
		return callback;
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

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
	public void optimize(Computation networkToOptimze, Iterator<? extends Sample> labeledSampleGenerator) {
		List<WeightGradStorage> grads = new ArrayList<>();
		while (labeledSampleGenerator.hasNext()) {
			grads.add(networkToOptimze.calculateWeightGrads(getLossFunction(), labeledSampleGenerator.next()));
			if (callback != null)
				callback.run();
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
