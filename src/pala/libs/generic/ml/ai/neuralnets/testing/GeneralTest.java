package pala.libs.generic.ml.ai.neuralnets.testing;

import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

import pala.libs.generic.JavaTools;
import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.LossFunction;
import pala.libs.generic.ml.ai.neuralnets.computations.WeightLayerNode;

public class GeneralTest {

	public static void main(String[] args) {
		Random r = new Random();

		int intermediaryNodes = 2;
		Computation net = Computation.chain(new WeightLayerNode(r, 3, intermediaryNodes),
				new WeightLayerNode(r, intermediaryNodes, intermediaryNodes),
				new WeightLayerNode(r, intermediaryNodes, intermediaryNodes),
				new WeightLayerNode(r, intermediaryNodes, intermediaryNodes), Computation.add(intermediaryNodes));
		LossFunction loss = LossFunction.meanSquaredError(1);

		// Function to learn
		int inputs = 3;
		ToDoubleFunction<double[]> funcToLearn = value -> value[0] * value[1] + value[2];

		// Rand generator
		DoubleSupplier ds = () -> Math.random() * 15;

		// Initial testing:
		double[] input = JavaTools.makeArray(inputs, ds);
		double trueResult = funcToLearn.applyAsDouble(input);
		double predictedResult = net.eval(input)[0];

		System.out.println("Initial Loss: " + loss.evalLoss(new double[] { trueResult }, predictedResult));

		// Training
		tryLearnFunction(funcToLearn, inputs, 50000, net, 0.00001, loss, ds);

		// Final testing.
		predictedResult = net.eval(input)[0];// Reevaluate
		System.out.println("Trained Loss: " + loss.evalLoss(new double[] { trueResult }, predictedResult));

		System.out.println("PREDICTIONS:");
		System.out.println();
		for (int i = 0; i < 5; i++) {
			System.out.println("Input:\t" + Arrays.toString(input = JavaTools.makeArray(inputs, ds)));
			System.out.println("\tAnswer:\t" + funcToLearn.applyAsDouble(input));
			System.out.println("\tGuess:\t" + net.eval(input)[0]);
		}
	}

	public static void tryLearnFunction(ToDoubleFunction<double[]> func, int inputs, int trainingIterations,
			Computation network, double learningRate, LossFunction loss, DoubleSupplier numberGen) {
		for (double[] d = JavaTools.makeArray(inputs,
				numberGen); trainingIterations > 0; trainingIterations--, d = JavaTools.makeArray(inputs, numberGen))
			network.train(loss, learningRate, new double[] { func.applyAsDouble(d) }, d);
	}
}
