package pala.libs.generic.ml.ai.neuralnets.computations;

import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;

public class SoftmaxComputation extends OneToOneComputation {

	public SoftmaxComputation(int inputs) {
		super(inputs);
	}

	@Override
	public double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		assert outGrad.length == outputs() : "Invalid gradient array size for SoftmaxComputation";
		double[] inputs = c.get(), res = new double[inputs()];
		for (int i = 0; i < res.length; i++)
			for (int j = 0; j < outGrad.length; j++)
				res[i] += outGrad[j] * inputs[i] * ((i == j ? 1 : 0) - inputs[i]);
		return res;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		assert input.length == inputs() : "Invalid input array size for SoftmaxComputation";

		c.set(input);
		double total = 0, res[] = new double[inputs()];

		for (int i = 0; i < input.length; i++)
			total += (res[i] = Math.exp(input[i]));
		for (int i = 0; i < res.length; i++)
			res[i] /= total;

		return res;
	}

}
