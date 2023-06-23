package pala.libs.generic.ml.ai.neuralnet4.computations;

import pala.libs.generic.ml.ai.neuralnet4.api.Computation;
import pala.libs.generic.ml.ai.neuralnet4.api.Container;
import pala.libs.generic.ml.ai.neuralnet4.api.WeightGradStorage;

public class SigmoidComputation implements Computation {

	private final int inputs;

	public SigmoidComputation(int inputs) {
		this.inputs = inputs;
	}

	@Override
	public int outputs() {
		return inputs;
	}

	@Override
	public int inputs() {
		return inputs;
	}

	@Override
	public double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		double[] res = new double[inputs], sig = c.get();
		assert sig.length == res.length && sig.length == outGrad.length && sig.length == inputs;
		for (int i = 0; i < inputs; i++)
			res[i] = sig[i] * (1 - sig[i]) * outGrad[i];
		return res;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		double[] res = new double[inputs];
		for (int i = 0; i < res.length; i++)
			res[i] = 1 / (1 + Math.exp(input[i]));
		c.set(res);
		return res;
	}

}
