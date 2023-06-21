package pala.libs.generic.ml.ai.neuralnet4;

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
public abstract class Node implements Computation {

	protected final double[] weights;
	private final int inputs, outputs;

	public Node(int inputs, int outputs, double... weights) {
		this.weights = weights;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public Node(int inputs, int outputs, int weights) {
		this(inputs, outputs, new double[weights]);
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

	public final void setWeight(int weight, double value) {
		weights[weight] = value;
	}

	public final double getWeight(int weight) {
		return weights[weight];
	}

}
