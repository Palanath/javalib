package pala.libs.generic.ml.ai.neuralnets.computations.scalar;

import pala.libs.generic.ml.ai.neuralnets.api.Container;

public class SigmoidComputation extends ScalarComputation {

	public SigmoidComputation(int inputs) {
		super(inputs);
	}

	@Override
	public double grad(Container c) {
		double sig = c.get();
		return sig * (1 - sig);
	}

	@Override
	public double eval(Container c, double input) {
		double sig = 1 / (1 + Math.exp(input));
		c.set(sig);
		return sig;
	}

}
