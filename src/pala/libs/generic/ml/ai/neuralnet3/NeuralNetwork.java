package pala.libs.generic.ml.ai.neuralnet3;

import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork {
	private double[][][] weights;

	public int getInputSize() {
		return weights[0].length;
	}

	public int getOutputSize() {
		return weights[weights.length - 1].length;
	}

	private static double[] applyWeights(double[] inputs, double[][] weights) {
		double[] res = new double[weights[0].length];
		for (int i = 0; i < inputs.length; i++)
			for (int j = 0; j < weights[0].length; j++)
				res[j] += inputs[i] * weights[i][j];
		return res;
	}

	private static double sigmoid(double input) {
		return 1 / (1 + Math.exp(-input));
	}

	private static double meanSquaredError(double[] v1, double... v2) {
		double e = 0;
		for (int i = 0; i < v1.length; i++) {
			double d = v1[i] - v2[i];
			e += d * d;
		}
		return e / v1.length;
	}

	private static double[] applyLayer(double[] inputs, double[][] layerWeights) {
		double[] res = applyWeights(inputs, layerWeights);
		for (int i = 0; i < res.length; i++)
			res[i] = sigmoid(res[i]);
		return res;
	}

	public double[] evaluate(double... input) {
		for (int i = 0; i < weights.length - 1; i++) {
			System.out.println("Running input of size: " + input.length + " through weight-layer of shape: "
					+ weights[i].length + " x " + weights[i][0].length);
			input = applyLayer(input, weights[i]);
		}
		System.out.println("Running input of size: " + input.length + " through weight-layer of shape: "
				+ weights[weights.length - 1].length + " x " + weights[weights.length - 1][0].length);
		input = applyWeights(input, weights[weights.length - 1]);
		return input;
	}

	public double[][] evaluate(double[]... inputs) {
		double[][] result = new double[inputs.length][];
		for (int i = 0; i < result.length; i++)
			result[i] = evaluate(inputs[i]);
		return result;
	}

	public double loss(double[] correctAnswer, double... input) {
		return meanSquaredError(evaluate(input), correctAnswer);
	}

	public double[][][] grad(double[] correctAnswer, double... input) {
		double[][] layerInputs = new double[weights.length + 1][];
		layerInputs[0] = input;
		for (int i = 1; i < layerInputs.length; i++)
			layerInputs[i] = applyLayer(layerInputs[i - 1], weights[i - 1]);
		double[] prediction = applyWeights(layerInputs[layerInputs.length - 1], weights[weights.length - 1]);

		// Derivative of loss w/ respect to each output neuron's output.
		double[] lgrad = subtractElementwise(prediction, correctAnswer);

		double[][][] grad = new double[weights.length][][];

		grad[weights.length - 1] = new double[layerInputs[layerInputs.length - 1].length][prediction.length];
		for (int i = 0; i < grad[weights.length - 1].length; i++)
			for (int j = 0; j < grad[weights.length - 1][i].length; j++)
				grad[weights.length - 1][i][j] = layerInputs[layerInputs.length - 1][i] * lgrad[j];

		// Hidden layer weight gradients
		double[][] nextLayerWeights = weights[weights.length - 1]; // Get the weights of the next layer
		for (int i = weights.length - 2; i >= 0; i--) {
			double[][] layerGradients = new double[layerInputs[i + 1].length][layerInputs[i].length];
			for (int j = 0; j < layerGradients.length; j++)
				for (int k = 0; k < layerGradients[j].length; k++) {
					double sum = 0;
					for (int n = 0; n < nextLayerWeights.length; n++)
						sum += nextLayerWeights[n][j] * lgrad[n];
					layerGradients[j][k] = layerInputs[i][k] * (1 - layerInputs[i][k]) * sum;
				}
			grad[i] = layerGradients;
			nextLayerWeights = weights[i];
			lgrad = new double[nextLayerWeights.length];
			for (int j = 0; j < nextLayerWeights.length; j++)
				for (int k = 0; k < layerGradients.length; k++)
					lgrad[j] += nextLayerWeights[j][k] * layerGradients[k][j];
		}

		return grad;

	}

	public void train(double learningRate, int times, double[] correctAnswer, double... input) {
		for (int i = 0; i < times; i++) {
			double[][][] g = grad(correctAnswer, input);
			for (int j = 0; j < weights.length; j++)
				for (int k = 0; k < weights[j].length; k++)
					for (int l = 0; l < weights[j][k].length; l++)
						weights[j][k][l] -= learningRate * g[j][k][l];
		}
	}

	private static double dotProd(double[] v1, double... v2) {
		double res = 0;
		for (int i = 0; i < v1.length; i++)
			res += v1[i] * v2[i];
		return res;
	}

	private static double[] scaleInPlace(double amt, double... input) {
		for (int i = 0; i < input.length; i++)
			input[i] *= amt;
		return input;
	}

	private static double[] subtractElementwise(double[] first, double[] second) {
		double res[] = new double[first.length];
		for (int i = 0; i < first.length; i++)
			res[i] = first[i] - second[i];
		return res;
	}

	/**
	 * Sets the weights of the network to random values between <code>0</code> and
	 * <code>1</code>.
	 */
	public void randomizeWeights() {
		randomizeWeights(new Random());
	}

	public void randomizeWeights(Random rand) {
		for (double[][] d : weights)
			randomize(rand, d);
	}

	private void randomize(Random rand, double[]... weights) {
		for (int i = 0; i < weights.length; i++)
			for (int j = 0; j < weights[i].length; j++)
				weights[i][j] = rand.nextDouble() * 2 - 1;
	}

	private void randomize(double[]... weights) {
		randomize(new Random(), weights);
	}

	/**
	 * <p>
	 * Creates a new, simple {@link NeuralNetwork} with the specified number of
	 * nodes in the input layer, output layer, and hidden layers. Each input node
	 * takes in exactly one input, so if the network is to accept data vectors of
	 * size 7 as input (for example), it will need 7 input nodes. Correspondingly,
	 * the number of output nodes will be the shape of the vector output of the
	 * network.
	 * </p>
	 * <p>
	 * Note that the weights in the network will all simply be initialized to
	 * <code>0</code>. To randomize the weights, {@link #randomizeWeights()} should
	 * be called after instantiating the {@link NeuralNetwork}.
	 * </p>
	 * 
	 * @param inputNodes  The number of input nodes (exactly equal to the size of
	 *                    the input vectors given to the network).
	 * @param outputNodes The number of output nodes (exactly equalt to the size of
	 *                    the output vectors returned by the network).
	 * @param hiddenNodes An array containing the number of hidden nodes for each
	 *                    hidden layer. If this is specified as
	 *                    <code>3, 5, 3</code>, then the first hidden layer will
	 *                    have 3 hidden nodes. The second will have 5, and the last
	 *                    will have 3. (The first hidden layer always receives input
	 *                    from the input layer, and the last hidden layer always
	 *                    gives its output to the output layer.)
	 */
	public NeuralNetwork(int inputNodes, int outputNodes, int... hiddenNodes) {
		if (hiddenNodes.length == 0)
			weights = new double[1][inputNodes][outputNodes];
		else {
			weights = new double[hiddenNodes.length + 1][][];
			weights[0] = new double[inputNodes][hiddenNodes[0]];
			for (int i = 1; i < weights.length - 1; i++)
				weights[i] = new double[hiddenNodes[i - 1]][hiddenNodes[i]];
			weights[weights.length - 1] = new double[hiddenNodes[hiddenNodes.length - 1]][outputNodes];
		}
	}

	public static void main(String[] args) {
		double[][][] trainingSamples = { { { 1 }, { 5 } }, { { 2 }, { 5 } }, { { 3 }, { 7 } } };
		NeuralNetwork nn = new NeuralNetwork(1, 1, 2, 2, 2);
		for (int i = 0; i < nn.weights.length; i++)
			System.out.println(Arrays.deepToString(nn.weights[i]));
		nn.randomizeWeights(new Random(5));
		System.out.println(Arrays.toString(nn.evaluate(1)));
		nn.train(0.01, 500, new double[] { 17 }, 1);
		System.out.println(Arrays.toString(nn.evaluate(1)));
	}

}
