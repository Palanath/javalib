package pala.libs.generic.ml.ai.neuralnet4;

public class WeightLayerNode extends Node {

	/**
	 * A computational {@link Node} that is synonymous to the weights between two
	 * layers in a standard neural network. The number of neurons in front of the
	 * weights (in the layer that data comes from) is specified by the
	 * <code>inputs</code> parameter. The number of neurons behind the weights (in
	 * the layer that data flows to, after passing through the weights) is specified
	 * by the <code>outputs</code> parameter. The number of actual weights in this
	 * {@link Node} is equivalent to <code>inputs * outputs</code>, since the inputs
	 * and outputs are fully connected to each other by weights.
	 * 
	 * @param inputs  The number of inputs.
	 * @param outputs The number of outputs.
	 */
	public WeightLayerNode(int inputs, int outputs) {
		super(inputs, outputs, inputs * outputs);
	}

	@Override
	public double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		// TODO Auto-generated method stub
		return null;
	}

}
