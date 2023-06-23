package pala.libs.generic.ml.ai.neuralnet4.testing;

import java.util.Random;

import pala.libs.generic.ml.ai.neuralnet4.Computation;
import pala.libs.generic.ml.ai.neuralnet4.LossFunction;
import pala.libs.generic.ml.ai.neuralnet4.WeightLayerNode;

public class Test3 {
	public static void main(String[] args) {
		Random r = new Random(13);
		Computation c = Computation.chain(new WeightLayerNode(r, 1, 1)// ,
//				new WeightLayerNode(r, 2, 3),
//				new WeightLayerNode(r, 3, 4), 
//				new WeightLayerNode(r, 4, 1)
		);
		for (int i = 0; i < 30; i++)
			c.train(LossFunction.meanSquaredError(1), .25d / (i + 1), new double[] { 1 }, 3);

	}
}
