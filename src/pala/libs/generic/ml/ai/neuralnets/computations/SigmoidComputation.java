package pala.libs.generic.ml.ai.neuralnets.computations;

import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;

public class SigmoidComputation extends OneToOneComputation {

	public SigmoidComputation(int inputs) {
		super(inputs);
	}

	@Override
	public double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		double[] res = new double[inputs()], sig = c.get();
		assert sig.length == res.length && sig.length == outGrad.length && sig.length == inputs();
		for (int i = 0; i < inputs(); i++)
			res[i] = sig[i] * (1 - sig[i]) * outGrad[i];
		return res;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		double[] res = new double[inputs()];
		for (int i = 0; i < res.length; i++)
			res[i] = 1 / (1 + Math.exp(input[i]));
		c.set(res);
		return res;
	}

}
