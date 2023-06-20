package pala.libs.generic.ml.ai.neuralnet4;

public interface Node {
	/**
	 * <p>
	 * Returns the number of scalars that this {@link Node} requires as input. The
	 * {@link Node} can be considered to take a vector of this size as input.
	 * </p>
	 * <p>
	 * {@link #evaluate(double...)} should only be called with this many input
	 * values.
	 * </p>
	 * 
	 * @return The number of input values that this {@link Node} accepts.
	 */
	int inputs();

	/**
	 * <p>
	 * Returns the number of scalars that this {@link Node} returns as output when
	 * evaluated.
	 * </p>
	 * <p>
	 * {@link #evaluate(double...)} returns this many <code>double</code>s.
	 * </p>
	 * 
	 * @return The number of output values of this {@link Node}.
	 */
	int outputs();

	/**
	 * Evaluates this {@link Node} on the provided input vector. The provided
	 * <code>input</code> vector should be of size {@link #inputs()} and the method
	 * will return an array of length {@link #outputs()}.
	 * 
	 * @param input The inputs.
	 * @return The outputs of evaluating the {@link Node} on the inputs.
	 */
	double[] evaluate(double... input);
}
