package pala.libs.generic.ml.ai.neuralnets.computations;

import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;

public class TanhComputation extends OneToOneComputation {

	public TanhComputation(int inputs) {
		super(inputs);
	}

	@Override
	public double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		assert inputs() == outGrad.length : "Invalid gradient vector len for TanhComputation.";
		double[] inputs = c.get(), res = new double[inputs()];
		for (int i = 0; i < res.length; i++) {
			double csh = Math.cosh(inputs[i]);
			res[i] = 1 / csh / csh;
		}
		return res;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		c.set(input);
		assert input.length == inputs() : "Invalid input array size for evaluation of TanhComputation.";
		double[] res = new double[outputs()];
		for (int i = 0; i < res.length; i++)
			res[i] = Math.tanh(input[i]);
		return res;
	}

}
