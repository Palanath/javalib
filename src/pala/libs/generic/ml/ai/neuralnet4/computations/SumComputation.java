package pala.libs.generic.ml.ai.neuralnet4.computations;

import java.util.Arrays;

import pala.libs.generic.JavaTools;
import pala.libs.generic.ml.ai.neuralnet4.api.Container;
import pala.libs.generic.ml.ai.neuralnet4.api.Operation;
import pala.libs.generic.ml.ai.neuralnet4.api.VectorComputation;
import pala.libs.generic.ml.ai.neuralnet4.api.WeightGradStorage;

public class SumComputation implements VectorComputation, Operation {

	private final int inputs;

	public SumComputation(int inputs) {
		this.inputs = inputs;
	}

	@Override
	public int inputs() {
		return inputs;
	}

	@Override
	public double[] evaluate(Container c, double... input) {
		assert input.length == inputs : "Invalid input for add computation.";
		return new double[] { JavaTools.sum(input) };
	}

	@Override
	public double[] grad(Container ctx, WeightGradStorage weightStorage, double... outGrad) {
		assert outGrad.length == 1 : "Invalid output gradient for add computation.";
		double[] res = new double[inputs];
		Arrays.fill(res, outGrad[0]);
		return res;
	}

}
