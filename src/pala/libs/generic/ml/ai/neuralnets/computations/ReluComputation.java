package pala.libs.generic.ml.ai.neuralnets.computations;

import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;

public class ReluComputation extends OneToOneComputation {

	public ReluComputation(int inputs) {
		super(inputs);
	}

	@Override
	public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
		boolean[] data = ctx.get();
		double[] res = new double[outGrad.length];
		for (int i = 0; i < res.length; i++)
			if (data[i])
				res[i] = outGrad[i];
		return res;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		double[] res = new double[input.length];
		boolean[] data = new boolean[input.length];
		for (int i = 0; i < input.length; i++)
			if (data[i] = (input[i] > 0))
				res[i] = input[i];
		c.set(data);
		return res;
	}

}
