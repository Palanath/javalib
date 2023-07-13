package pala.libs.generic.ml.ai.neuralnets.computations.scalar;

import pala.libs.generic.ml.ai.neuralnets.api.Computation;
import pala.libs.generic.ml.ai.neuralnets.api.Container;
import pala.libs.generic.ml.ai.neuralnets.api.ContainerImpl;
import pala.libs.generic.ml.ai.neuralnets.api.WeightGradStorage;
import pala.libs.generic.ml.ai.neuralnets.computations.OneToOneComputation;

/**
 * <p>
 * A {@link Computation} of a scalar function, possibly on multiple inputs.
 * Subclasses need only define the scalar function and its derivative with
 * respect to its input.
 * </p>
 * <p>
 * {@link ScalarComputation}s have the same number of inputs as outputs and are
 * more specific than {@link OneToOneComputation}s in that each output only
 * depends on its corresponding input. A {@link OneToOneComputation} has equal
 * number of outputs and inputs, but each output can depend on any inputs.
 * </p>
 * <p>
 * This class handles computation of the gradient step in its
 * {@link #grad(Container, WeightGradStorage, double...)} method based off of
 * the derivative calculation defined in {@link #grad(Container)} by subtypes.
 * This class's definition of
 * {@link #grad(Container, WeightGradStorage, double...)} calls
 * {@link #grad(Container)} and multiplies the result by the corresponding
 * incoming gradient value from <code>outGrad</code>.
 * </p>
 * 
 * @author Palanath
 *
 */
public abstract class ScalarComputation extends OneToOneComputation {

	public ScalarComputation(int inputs) {
		super(inputs);
	}

	@Override
	public final double[] grad(Container c, WeightGradStorage weightStorage, double... outGrad) {
		assert outGrad.length == outputs();

		ContainerImpl[] subcontainers = c.get();
		double[] res = outGrad.clone();
		for (int i = 0; i < res.length; i++)
			res[i] *= grad(subcontainers[i].disableModification());
		return res;
	}

	@Override
	public final double[] evaluate(Container c, double... input) {
		assert input.length == inputs();
		ContainerImpl[] subcontainers = new ContainerImpl[input.length];
		c.set(subcontainers);
		double[] res = new double[input.length];
		for (int i = 0; i < input.length; i++)
			res[i] = eval(subcontainers[i] = new ContainerImpl(), input[i]);
		return res;
	}

	/**
	 * Computes the derivative of this function given the provided
	 * {@link Container}.
	 * 
	 * @param c The container from an invocation of the forward computation
	 *          ({@link #eval(Container, double)}).
	 * @return A backward pass of the forward pass corresponding to the provided
	 *         {@link Container}.
	 */
	public abstract double grad(Container c);

	/**
	 * Evaluates this scalar function on the provided input. The {@link Container}
	 * is provided so that the computation can store an arbitrary datum that can be
	 * used for the backward pass computation that invokes {@link #grad(Container)}.
	 * 
	 * @param c     The {@link Container} for storing extra data.
	 * @param input The input value.
	 * @return The result of the scalar function evaluated on the input.
	 */
	public abstract double eval(Container c, double input);

}
