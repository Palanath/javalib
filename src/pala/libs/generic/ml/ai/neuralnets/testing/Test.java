package pala.libs.generic.ml.ai.neuralnets.testing;

import java.util.Arrays;
import java.util.Random;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.computations.WeightLayerNode;

public class Test {

	private static void print(Computation c, double... values) {
		System.out.println(Arrays.toString(c.eval(values)));
	}

	public static void main(String[] args) {
		Random r = new Random(2);
		WeightLayerNode l1 = new WeightLayerNode(r, 2, 100), l2 = new WeightLayerNode(r, 10, 10),
				l3 = new WeightLayerNode(r, 10, 10);
		Computation network = Computation.chain(l1, Computation.add(100));

//		int intermediarySize = 1;
//		Computation network = Computation.chain(new WeightLayerNode(2, intermediarySize), Computation.sigmoid(intermediarySize), new WeightLayerNode(intermediarySize, 1));

		print(network, 3, 5);
		print(network, 1, 2);
		print(network, 2, 9);

		train(5000, network);

		System.out.println("Trained");

		print(network, 3, 5);
		print(network, 1, 2);
		print(network, 2, 9);

//		train(50, network);
//		System.out.println("Trained 50 times");
//
//		print(network, 3, 5);
//		print(network, 1, 2);
//		print(network, 2, 9);
//
//		train(2000, network);
//		System.out.println("Trained 2000 times");
//
//		print(network, 3, 5);
//		print(network, 1, 2);
//		print(network, 2, 9);
	}

	private static void train(int times, Computation net) {
		for (; times > 0; times--)
			train(net);
	}

	private static void train(Computation network) {
		double r1 = Math.random() * 10, r2 = Math.random() * 10;
		network.train(LossFunction.meanSquaredError(1), 0.0001, new double[] { r1 + r2 }, r1, r2);
	}

}
