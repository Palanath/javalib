package pala.libs.generic.ml.ai.neuralnet2;

public class NeuralNetwork {
	private final double[][][] weights;

	/**
	 * Performs the dot product of the <code>inputVec</code> with each of the
	 * <code>dotVecs</code> and stores each <code>double</code> result in the
	 * returned <code>double</code> array. The order of the <code>double</code>s in
	 * the result is the same as the <code>double[]</code> <code>dotVecs</code>
	 * provided.
	 * 
	 * @param inputVec The input vector to dot product with each vector in
	 *                 <code>dotVecs</code>.
	 * @param dotVecs  The vectors to dot-product with <code>inputVec</code>.
	 * @return The result of the dot products.
	 */
	private static double[] dotEach(double inputVec[], double[]... dotVecs) {
		double[] res = new double[dotVecs.length];// One result per vector being dotted.

		for (int i = 0; i < dotVecs.length; i++)
			// For each vector being dotted, calc the dot product:
			for (int j = 0; j < dotVecs[i].length; j++)// Go over each component of inputVec and of dotVecs[i].
				res[i] += inputVec[j] * dotVecs[i][j];
		return res;
	}

	/**
	 * Overwrites each value in the provided array of <code>double</code>s with the
	 * sigmoid of that value. The provided, modified array is returned.
	 * 
	 * @param values The values to sigmoid.
	 * @return The given array, after it is modified.
	 */
	private static double[] sigmoidEachInPlace(double... values) {
		for (int i = 0; i < values.length; i++)
			values[i] = 1 / (1 + Math.exp(-values[i]));
		return values;
	}

	/**
	 * Performs the mathematical sigmoid function on each provided
	 * <code>double</code> and returns the results as an array.
	 * 
	 * @param values The values to sigmoid.
	 * @return The result of each application of the sigmoid function.
	 */
	private static double[] sigmoidEach(double... values) {
		return sigmoidEachInPlace(values.clone());
	}

	/**
	 * Sets the weights of the network to random values between <code>0</code> and
	 * <code>1</code>.
	 */
	public void randomizeWeights() {
		for (double[][] d : weights)
			randomize(d);
	}

	private void randomize(double[]... weights) {
		for (int i = 0; i < weights.length; i++)
			for (int j = 0; j < weights[i].length; j++)
				weights[i][j] = Math.random();
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
			for (int i = 1; i < weights.length; i++)
				weights[i] = new double[hiddenNodes[i - 1]][hiddenNodes[i]];
			weights[weights.length - 1] = new double[hiddenNodes[hiddenNodes.length - 1]][outputNodes];
		}
	}

}
