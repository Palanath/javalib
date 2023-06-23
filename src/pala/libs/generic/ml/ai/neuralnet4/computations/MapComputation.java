package pala.libs.generic.ml.ai.neuralnet4.computations;

import pala.libs.generic.ml.ai.neuralnet4.api.Computation;
import pala.libs.generic.ml.ai.neuralnet4.api.Container;
import pala.libs.generic.ml.ai.neuralnet4.api.WeightGradStorage;

public class MapComputation implements Computation {

	private final int inputs, mapping[];

	public MapComputation(int inputs, int... mapping) {
		this.inputs = inputs;
		this.mapping = mapping;
	}

	@Override
	public int outputs() {
		return mapping.length;
	}

	@Override
	public int inputs() {
		return inputs;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		double[] res = new double[mapping.length];
		for (int i = 0; i < mapping.length; i++)
			res[i] = input[mapping[i]];
		return res;
	}

	@Override
	public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
		// Each output that a single input is sent to increases the gradient of that
		// input by the gradient of the output.
		// If one input points to two outputs, with derivatives 5 and 3, respectively,
		// the input's derivative will be 8.
		double[] g = new double[inputs];
		for (int i = 0; i < mapping.length; i++)
			g[mapping[i]] += outGrad[i];
		return g;
	}

}
