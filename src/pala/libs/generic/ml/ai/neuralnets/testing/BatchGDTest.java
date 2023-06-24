package pala.libs.generic.ml.ai.neuralnets.testing;

import java.util.function.IntToDoubleFunction;

import pala.libs.generic.JavaTools;
import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.api.Node;
import pala.libs.generic.ml.ai.neuralnets.computations.ChainComputation;
import pala.libs.generic.ml.ai.neuralnets.computations.WeightLayerNode;
import pala.libs.generic.ml.ai.neuralnets.optimizers.AdaptiveRateGradientDescentOptimizer;
import pala.libs.generic.ml.ai.neuralnets.optimizers.AdaptiveRateGradientDescentOptimizer.BestRateSaver;
import pala.libs.generic.ml.ai.neuralnets.optimizers.BatchGradientDescentOptimizer;
import pala.libs.generic.util.Pair;

public class BatchGDTest {

	public interface FuncToLearn {
		double execute(double input);
	}

	public static Pair<double[], double[]>[] pckge(FuncToLearn func, double... inputs) {
		Pair<double[], double[]>[] res = JavaTools.array(inputs.length);
		for (int i = 0; i < res.length; i++)
			res[i] = new Pair<>(new double[] { inputs[i] }, new double[] { func.execute(inputs[i]) });
		return res;
	}

	public static void main(String[] args) {
		IntToDoubleFunction populator = a -> Math.random() * .2 + .9;
		LossFunction loss = LossFunction.meanSquaredError(1);

		Node f = new WeightLayerNode(1, 10), s = new WeightLayerNode(10, 10), t = new WeightLayerNode(10, 1);
		Computation c = new ChainComputation(f, s, t);
		f.populateWeights(populator);
		s.populateWeights(populator);
		t.populateWeights(populator);

		FuncToLearn func = a -> a * 2;

		double inp = Math.random();
		double netguess = c.eval(inp)[0];
		System.out.println("Original Loss: " + loss.evalLoss(new double[] { func.execute(inp) }, netguess));

		BestRateSaver brs = new BestRateSaver();

		AdaptiveRateGradientDescentOptimizer opt = new AdaptiveRateGradientDescentOptimizer(loss, 12, brs);
		opt.optimize(c, pckge(func, .42));
		System.out.println("Best learning rate: " + (brs.hasRate() ? brs.getBestRate() : "None"));
		BatchGradientDescentOptimizer bgd = new BatchGradientDescentOptimizer(loss,
				brs.hasRate() ? brs.getAndResetBestRate() : 0.000001);
		bgd.optimize(300, c, pckge(func, .101, .4, .8, .3, .9, .15, .82, .653, .752, .8125));

		opt.optimize(c, pckge(func, .42));
		System.out.println("Best learning rate: " + (brs.hasRate() ? brs.getBestRate() : "None"));
		bgd.setLearningRate(brs.hasRate() ? brs.getAndResetBestRate() : 0.000001);
		bgd.optimize(1, c, pckge(func, .101, .4, .8, .3, .9, .15, .82, .653, .752, .8125));

		netguess = c.eval(inp)[0];
		System.out.println("New Loss: " + loss.evalLoss(new double[] { func.execute(inp) }, netguess));

	}

}
