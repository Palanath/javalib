package pala.libs.generic.ml.ai.neuralnets.api.scalarfunctions;

public class SecantFunction implements ScalarFunction {

	@Override
	public double eval(double input) {
		return 1 / Math.cos(input);
	}

	@Override
	public double derivative(double input) {
		return Math.tan(input) / Math.cos(input);
	}

}
