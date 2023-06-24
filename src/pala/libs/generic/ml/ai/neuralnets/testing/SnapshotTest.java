package pala.libs.generic.ml.ai.neuralnets.testing;

import java.util.Arrays;
import java.util.Random;

import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.computations.ChainComputation;
import pala.libs.generic.ml.ai.neuralnets.computations.WeightLayerNode;
import pala.libs.generic.ml.ai.neuralnets.optimizers.AdaptiveRateGradientDescentOptimizer;
import pala.libs.generic.ml.ai.neuralnets.optimizers.GradientDescentOptimizer;
import pala.libs.generic.ml.ai.neuralnets.optimizers.Optimizer;
import pala.libs.generic.util.Box;
import pala.libs.generic.util.Pair;

public class SnapshotTest {
	public static void main(String[] args) {
		LossFunction lossFunction = LossFunction.meanSquaredError(1);
		final int input = 1, correctAnswer = 1;
		Pair<double[], double[]> sample = new Pair<>(new double[] { correctAnswer }, new double[] { input });

		Random r = new Random(14);

		// Prepare network.
		WeightLayerNode c = new WeightLayerNode(1, 5), c2 = new WeightLayerNode(5, 5), c3 = new WeightLayerNode(5, 5),
				c4 = new WeightLayerNode(5, 1);
		c.populateWeights(a -> r.nextDouble() * 0.2 + 0.9);
		c2.populateWeights(a -> r.nextDouble() * 0.2 + 0.9);
		c3.populateWeights(a -> r.nextDouble() * 0.2 + 0.9);
		c4.populateWeights(a -> r.nextDouble() * 0.2 + 0.9);
		ChainComputation res = new ChainComputation(c, c2, c3, c4);

		// Demo w/o training.

		double guess = res.eval(input)[0];
		System.out.println("Guess: " + guess);
		System.out.println("Answer: " + correctAnswer);
		System.out.println("Error Measure: " + lossFunction.evalLoss(new double[] { correctAnswer }, guess));

		// Train using learning rate finder then normal GD.

		System.out.println();
		Box<Double> bestLearningRate = new Box<>();
		Optimizer opt = new AdaptiveRateGradientDescentOptimizer(lossFunction, 4, (net, s, learningRate) -> {
			System.out.println("Best learning rate found: " + learningRate.get(learningRate.size() - 1));
			bestLearningRate.value = learningRate.get(learningRate.size() - 1).learningRate;
		});
		opt.optimize(4, res, sample);

		opt = new GradientDescentOptimizer(lossFunction, bestLearningRate.value);
		opt.optimize(20, res, sample);

		// Reevaluate

		System.out.println();
		System.out.println("Total training iterations: 24");

		guess = res.eval(input)[0];
		System.out.println();
		System.out.println("Guess: " + guess);
		System.out.println("Answer: " + correctAnswer);
		System.out.println("Error Measure: " + lossFunction.evalLoss(new double[] { correctAnswer }, guess));
	}
}
