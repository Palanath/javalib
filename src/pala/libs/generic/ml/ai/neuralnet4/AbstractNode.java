package pala.libs.generic.ml.ai.neuralnet4;

/**
 * <p>
 * A {@link Node} that contains weights. This class manages and stores:
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
 * <code>0</code> using {@link #AbstractNode(int, int, int)}.
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
public abstract class AbstractNode implements Node {

	protected final double[] weights;
	private final int inputs, outputs;

	public AbstractNode(int inputs, int outputs, double... weights) {
		this.weights = weights;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public AbstractNode(int inputs, int outputs, int weights) {
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

	@Override
	public final int weights() {
		return weights.length;
	}

	@Override
	public final void setWeight(int weight, double value) {
		weights[weight] = value;
	}

	@Override
	public final double getWeight(int weight) {
		return weights[weight];
	}

}
