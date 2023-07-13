package pala.libs.generic.ml.ai.neuralnets.computations.scalar;

import pala.libs.generic.ml.ai.neuralnets.api.Container;

public class TanhComputation extends ScalarComputation {

	public TanhComputation(int inputs) {
		super(inputs);
	}

	@Override
	public double grad(Container c) {
		double tanh = c.get();
		return 1 - tanh * tanh;
	}

	@Override
	public double eval(Container c, double input) {
		double res = Math.tanh(input);
		c.set(res);
		return res;
	}

}
