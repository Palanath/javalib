package pala.libs.generic.ml.ai.neuralnets.computations;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;

public interface GenericMapComputation extends Computation {

	@Override
	default double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		double[] grad = new double[inputs()];
		for (int i = 0; i < grad.length; i++)
			grad[map(i)] += outGrad[i];
		return grad;
	}

	/**
	 * <p>
	 * Returns the input value to draw the value of the output from. This method
	 * encodes the mapping from inputs to outputs.
	 * </p>
	 * <p>
	 * If the value passed into input <code>3</code> is to be mapped to output
	 * <code>7</code>, this method will return <code>3</code> when called with value
	 * <code>7</code>.
	 * </p>
	 * <p>
	 * When {@link #eval(double...)} is called, it iterates over every output and
	 * grabs the value from the input indicated by this method. The value is placed
	 * in the output array.
	 * </p>
	 * <p>
	 * Not every input needs to be returned by some call to this method.
	 * </p>
	 * 
	 * @param output The output to get the input mapping for.
	 * @return The index of the input element that provides the value for this
	 *         output.
	 */
	int map(int output);

	@Override
	default double[] evaluate(Container c, double... input) {
		double[] output = new double[outputs()];
		for (int i = 0; i < output.length; i++)
			output[i] = input[map(i)];
		return output;
	}

	interface Mapper {
		int map(int output);
	}

	static GenericMapComputation map(int inputs, int outputs, Mapper mapper) {
		return new GenericMapComputation() {

			@Override
			public int outputs() {
				return outputs;
			}

			@Override
			public int inputs() {
				return inputs;
			}

			@Override
			public int map(int output) {
				return mapper.map(output);
			}
		};
	}

}
