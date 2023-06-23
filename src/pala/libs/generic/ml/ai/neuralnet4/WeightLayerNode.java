package pala.libs.generic.ml.ai.neuralnet4;

import java.util.Random;

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
 * @author Palanath
 *
 */
public class WeightLayerNode extends Node {

	public WeightLayerNode(Random weightRandomizer, int inputs, int outputs) {
		super(weightRandomizer, inputs, outputs, inputs * outputs);
	}

	public WeightLayerNode(int inputs, int outputs) {
		super(inputs, outputs, inputs * outputs);
	}

	@Override
	public double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		assert outGrad.length == outputs() : "WeightLayerNode received invalid gradient from subsequent node.";
		double[] inputs = c.get(), res = new double[inputs()], weightGrad = new double[weights.length];
		weightStorage.put(this, weightGrad);
		for (int i = 0; i < res.length; i++) {
			// dL/dI_i = SUM from (x=0) to (outputs()) of (dO_x/dI_i * dL/dO_x)
			//
			// dO_x/dI_i is the change in output x w.r.t. input i, which is simply equal to
			// the weight connecting those two nodes.
			//
			// dL/dO_x is equal to outGrad[x]
			//
			// So we have that dL/dI_i = SUM from (x=0) to (outputs()) of (w_[i->x] *
			// outGrad[x])
			//
			// where w_[i->x] is the weight going from input node i to output node x. That
			// is element i*outputs()+x in the weights array.

			for (int x = 0; x < outputs(); x++) {
				res[i] += weights[i * outputs() + x] * outGrad[x];

				// Also, the change in any weight, w_[i->x], with respect to the output its
				// connection goes to, O_x, is simply going to be the input that the datum comes
				// from: dO_x/dw_[i->x] = I_i
				weightGrad[i * outputs() + x] = inputs[i] * outGrad[x];
			}
		}
		return res;
	}

	public final double getWeight(int input, int output) {
		return weights[input * outputs() + output];
	}

	public final void setWeight(int input, int output, double newValue) {
		weights[input * outputs() + output] = newValue;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		assert input.length == inputs() : "Invalid array size for input to WeightLayerNode. Expected: " + inputs()
				+ " but received " + input.length;
		c.set(input);
		final int inputs = inputs();
		double[] out = new double[outputs()];
		for (int i = 0; i < inputs; i++)
			for (int j = 0; j < out.length; j++)
				out[j] += input[i] * weights[i * out.length + j];
		return out;
	}

}
