package pala.libs.generic.ml.ai.neuralnets.api.scalarfunctions;

public class TangentFunction implements ScalarFunction {

	@Override
	public double eval(double input) {
		return Math.tan(input);
	}

	@Override
	public double derivative(double input) {
		double sec = 1 / Math.cos(input);
		return sec * sec;
	}

}
