package pala.libs.generic.ml.ai.neuralnets.testing;

import java.util.Arrays;

import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.computations.WeightLayerNode;

public class Test2 {
	public static void main(String[] args) {
		WeightLayerNode c = new WeightLayerNode(1, 1);
		c.setWeight(0, 0, 1);

		int answer = 2;

		System.out.println("INPUT: " + 1);
		System.out.println("GUESS: " + Arrays.toString(c.eval(1)));
		System.out.println("ANSWER: " + answer);
		for (int i = 0; i < 100; i++)
			c.train(LossFunction.meanSquaredError(1), 1, new double[] { answer }, 1);// Oscillates with learning rate
																						// 1.0
		System.out.println(Arrays.toString(c.eval(1)));
	}
}
