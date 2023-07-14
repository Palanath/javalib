package pala.libs.generic.ml.ai.neuralnets.api.scalarfunctions;

public class ExpFunction implements ScalarFunction {

	@Override
	public double eval(double input) {
		return Math.exp(input);
	}

	@Override
	public double derivative(double input) {
		return Math.exp(input);
	}

}
