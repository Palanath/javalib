package pala.libs.generic.ml.ai.neuralnets.computations;

import pala.libs.generic.ml.ai.neuralnets.api.Container;

public class ReluComputation extends ScalarComputation {

	public ReluComputation(int inputs) {
		super(inputs);
	}

	@Override
	public double grad(Container c) {
		return (boolean) c.get() ? 1 : 0;
	}

	@Override
	public double eval(Container c, double input) {
		boolean x = input > 0;
		c.set(x);
		return x ? input : 0;
	}

}
