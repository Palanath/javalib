package pala.libs.generic.ml.ai.neuralnets.api.scalarfunctions;

public class CosineFunction implements ScalarFunction {

	@Override
	public double eval(double input) {
		return Math.cos(input);
	}

	@Override
	public double derivative(double input) {
		return -Math.sin(input);
	}

}
