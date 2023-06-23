package pala.libs.generic.ml.ai.neuralnet4.computations;

import pala.libs.generic.ml.ai.neuralnet4.api.Container;
import pala.libs.generic.ml.ai.neuralnet4.api.VectorComputation;
import pala.libs.generic.ml.ai.neuralnet4.api.WeightGradStorage;

public class ProductComputation implements VectorComputation {

	private final int inputs;

	public ProductComputation(int inputs) {
		this.inputs = inputs;
	}

	@Override
	public int inputs() {
		return inputs;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		assert input.length == inputs : "Invalid input for multiply computation.";
		c.set(input);
		double res = 1;
		for (int i = 0; i < input.length; i++)
			res *= input[i];
		return new double[] { res };
	}

	@Override
	public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
		assert outGrad.length == 1 : "Invalid output gradient for multiply computation.";

		// Derivative of input is each of the other inputs.
		double[] ins = ctx.get();
		double m = outGrad[0];// outputDeriv times each input
		for (int i = 0; i < inputs; i++)
			m *= ins[i];

		double[] g = new double[inputs];
		for (int i = 0; i < g.length; i++)
			g[i] = m / ins[i];// division removes "this input" from the total multiplication
		return g;
	}

}
