package pala.libs.generic.ml.ai.neuralnet4.api;

import java.util.Arrays;
import java.util.Random;
import java.util.function.IntToDoubleFunction;

/**
 * <p>
 * A {@link Computation} that contains state, in the form of weights. Weights
 * are scalar (<code>double</code>) values, stored by the {@link Node}, that
 * affect the output of the {@link Node}. The gradient of loss with respect to a
 * {@link Node}'s weights can be calculated, as well, since the output of a
 * {@link Node} should be a differentiable function of the inputs and weights of
 * that {@link Node}.
 * </p>
 * <p>
 * This class manages and stores:
 * </p>
 * <ol>
 * <li>Weights</li>
 * <li>Input Count</li>
 * <li>Output Count</li>
 * </ol>
 * <p>
 * The input and output counts, as well as the number of weights, must be
 * specified at construction. The weights themselves can be given an initial
 * value using the {@link #AbstractNode(int, int, double...)} constructor, or
 * the number of weights can be specified and they all be initialized to
 * <code>0</code> using {@link #Node(int, int, int)}.
 * </p>
 * <p>
 * This class overrides the {@link #inputs()}, {@link #outputs()},
 * {@link #weights()}, {@link #getWeight(int)}, and
 * {@link #setWeight(int, double)} methods and uses a <code>double</code> array,
 * containing values for the specified number of weights, to back them.
 * </p>
 * 
 * @author Palanath
 *
 */
public abstract class Node implements Computation, Snapshottable {

	protected final double[] weights;
	private final int inputs, outputs;

	public void populateWeights(IntToDoubleFunction populator) {
		for (int i = 0; i < weights.length; i++)
			weights[i] = populator.applyAsDouble(i);
	}

	public Node(int inputs, int outputs, double... weights) {
		this(false, inputs, outputs, weights);
	}

	public Node(int inputs, int outputs, int weights) {
		this(inputs, outputs, new double[weights]);
	}

	public static void randomize(Random rand, double... array) {
		for (int i = 0; i < array.length; i++)
			array[i] = rand.nextDouble() * 2 - 1;
	}

	/**
	 * Creates a new {@link Node} with the specified array of <code>double</code>s
	 * as the weight array and randomizes the values of the array with the provided
	 * <code>Random</code>. The array provided is not copied, it is used as is, so
	 * external changes to it are reflected by the {@link Node}. Care should be
	 * taken not to edit the array at inappropriate times (asynchronously during a
	 * forward or backward pass, or between a forward and backward pass).
	 * 
	 * @param weightRandomizer The {@link Random} object used to randomize the
	 *                         weights. Can be <code>null</code>, in which case, the
	 *                         weights are not randomized. This is only used during
	 *                         construction of the object (if provided).
	 * @param inputs           The number of inputs in the {@link Node}.
	 * @param outputs          The number of outputs in the {@link Node}.
	 * @param weights          The weights of the {@link Node}.
	 */
	public Node(Random weightRandomizer, int inputs, int outputs, double... weights) {
		if (weightRandomizer != null)
			randomize(weightRandomizer, weights);
		this.weights = weights;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public void randomizeWeights(Random random) {
		randomize(random, weights);
	}

	public void randomizeWeights() {
		randomizeWeights(new Random());
	}

	public Node(Random weightRandomizer, int inputs, int outputs, int weights) {
		this(weightRandomizer, inputs, outputs, new double[weights]);
	}

	public Node(boolean randomizeWeights, int inputs, int outputs, double... weights) {
		this(randomizeWeights ? new Random() : null, inputs, outputs, weights);
	}

	public Node(boolean randomizeWeights, int inputs, int outputs, int weights) {
		this(false, inputs, outputs, new double[weights]);
	}

	@Override
	public final int inputs() {
		return inputs;
	}

	@Override
	public final int outputs() {
		return outputs;
	}

	public final int weights() {
		return weights.length;
	}

	public void save(Snapshot snapshot) {
		snapshot.getWeightMapping().put(this, Arrays.copyOf(weights, weights.length));
	}

	public void restore(Snapshot snapshot) {
		double[] ws = snapshot.getWeightMapping().get(this);
		assert ws != null && ws.length == weights.length;
		System.arraycopy(snapshot.getWeightMapping().get(this), 0, weights, 0, weights.length);
	}

	/**
	 * Returns the array of weights backing this {@link Node}. The array can be
	 * modified and changes will be reflected by the {@link Node}.
	 * 
	 * @return The backing weights of this {@link Node}.
	 */
	public final double[] getBackingWeights() {
		return weights;
	}

}
