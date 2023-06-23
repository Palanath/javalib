package pala.libs.generic.ml.ai.neuralnet4.testing;

import java.util.Arrays;
import java.util.Random;

import pala.libs.generic.ml.ai.neuralnet4.api.Computation;
import pala.libs.generic.ml.ai.neuralnet4.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnet4.computations.WeightLayerNode;

public class ExplosionTest {

	public static void main(String[] args) {
		Random r = new Random();

		double i1 = 1, i2 = 1, correctAnswer[] = new double[] { i1 + i2 };
		LossFunction loss = LossFunction.meanSquaredError(1);

		WeightLayerNode wl1 = new WeightLayerNode(2, 2), wl2 = new WeightLayerNode(2, 2);
		Computation c = Computation.chain(wl1, wl2, Computation.add(2));
		for (int i = 0; i < wl1.getBackingWeights().length; i++)
			wl1.getBackingWeights()[i] = i + 1;
		for (int i = 0; i < wl2.getBackingWeights().length; i++)
			wl2.getBackingWeights()[i] = wl2.weights() + i + 1;

		System.out.println(loss.evalLoss(correctAnswer, c.eval(i1, i2)));
		c.train(loss, 1.5, correctAnswer, i1, i2);
		System.out.println(loss.evalLoss(correctAnswer, c.eval(i1, i2)));
	}

}
